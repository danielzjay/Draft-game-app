package com.example.auth

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import java.security.SecureRandom
import android.util.Log
import android.os.Build
import java.security.MessageDigest

/**
 * Real Google Sign-In, using the modern Credential Manager API (the old GoogleSignInClient /
 * GoogleSignInOptions APIs are deprecated as of 2025). This shows Google's own system-rendered
 * account picker — the app itself never draws an "account chooser" UI, since anything an app
 * draws itself claiming to be a Google account picker is indistinguishable from a phishing screen.
 *
 * Setup required for this to actually work:
 *  1. Firebase Console → Authentication → Sign-in method → enable "Google".
 *  2. Your app's SHA-1 must be registered against this Firebase Android app (already done).
 *  3. WEB_CLIENT_ID below must be the "client_type": 3 entry from your google-services.json
 *     (the auto-generated Web client, NOT the Android client). It's already filled in from the
 *     file you gave me — if you ever regenerate google-services.json, re-check this value.
 *
 * TROUBLESHOOTING NOTE: "the picker shows and I can pick an account, but the app doesn't sign
 * in" almost always means the account picker step (handled by Google Play Services) succeeded,
 * but the LATER step — Firebase verifying the ID token and creating a session — failed. That
 * second step is a completely separate point of failure from the picker itself, and the most
 * common causes are: (a) WEB_CLIENT_ID below doesn't match the current google-services.json
 * exactly, especially if you regenerated it after this was written, (b) the Google provider
 * isn't actually enabled in Firebase Console → Authentication → Sign-in method, or (c) stale
 * Google Play Services cache on the test device (Settings → Apps → Google Play Services →
 * Storage → Clear cache). The error surfaced by this function's Result.failure should say
 * which — that message is now shown persistently in the sign-in dialog instead of a toast that
 * can be missed, specifically so the real cause is visible next time this happens.
 */
object GoogleAuthManager {

    // From google-services.json -> client[0].oauth_client[] where client_type == 3
    private const val WEB_CLIENT_ID =
        "861274979266-fji7kfo11r23ihjhlq5blsh2903lvuj3.apps.googleusercontent.com"

    /**
     * Gets the current app's SHA-1 signature fingerprint dynamically to assist in debugging.
     */
    fun getAppSignatureSHA1(context: Context): String {
        return try {
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                context.packageManager.getPackageInfo(
                    context.packageName,
                    android.content.pm.PackageManager.GET_SIGNING_CERTIFICATES
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(
                    context.packageName,
                    android.content.pm.PackageManager.GET_SIGNATURES
                )
            }
            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.signingInfo?.apkContentsSigners
            } else {
                @Suppress("DEPRECATION")
                packageInfo.signatures
            }
            if (signatures != null && signatures.isNotEmpty()) {
                val md = MessageDigest.getInstance("SHA-1")
                val publicKey = md.digest(signatures[0].toByteArray())
                publicKey.joinToString(":") { "%02X".format(it) }
            } else {
                "No certificates found"
            }
        } catch (e: Exception) {
            Log.e("GoogleAuth", "Failed to retrieve package SHA-1 signature", e)
            "Error: ${e.message}"
        }
    }

    suspend fun signIn(context: Context, onLog: (String) -> Unit = {}): Result<FirebaseUser> {
        val packageName = context.packageName
        val appSha1 = getAppSignatureSHA1(context)
        
        Log.d("GoogleAuth", "--- START GOOGLE SIGN IN TRACE ---")
        Log.d("GoogleAuth", "Package Name: $packageName")
        Log.d("GoogleAuth", "App SHA-1: $appSha1")
        Log.d("GoogleAuth", "Target Web Client ID: $WEB_CLIENT_ID")
        
        onLog("Initializing Google Sign-In sequence...")
        onLog("Package: $packageName")
        onLog("Active Signature (SHA-1):\n$appSha1")
        onLog("Target Web Client ID:\n$WEB_CLIENT_ID")

        return try {
            onLog("Initializing modern Android Credential Manager...")
            val credentialManager = CredentialManager.create(context)

            onLog("Configuring Google Sign-In Builder option (with filter disabled)...")
            val signInOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(WEB_CLIENT_ID)
                .setNonce(generateNonce())
                .setAutoSelectEnabled(false)
                .build()

            onLog("Assembling credential request payload...")
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(signInOption)
                .build()

            onLog("Displaying Google Accounts bottom-sheet overlay...")
            val result = credentialManager.getCredential(context, request)
            onLog("System account selection callback received!")
            
            val credential: Credential = result.credential

            if (credential !is CustomCredential || credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val errText = "Unexpected credential type returned: ${credential.type}"
                Log.e("GoogleAuth", errText)
                onLog("[FATAL] $errText")
                return Result.failure(Exception(errText))
            }

            onLog("Parsing identity credential data payload...")
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            
            onLog("Retrieving OAuth2 ID token from credential payload...")
            val rawIdToken = googleIdTokenCredential.idToken
            Log.d("GoogleAuth", "Successfully retrieved Google ID token (length: ${rawIdToken.length})")
            
            onLog("Acquiring Firebase Auth credential object...")
            val firebaseCredential = GoogleAuthProvider.getCredential(rawIdToken, null)

            onLog("Authenticating with Firebase services...")
            val authResult = FirebaseAuth.getInstance().signInWithCredential(firebaseCredential).await()
            val user = authResult.user
            if (user == null) {
                val errText = "Firebase sign-in completed but returned an empty user profile."
                Log.e("GoogleAuth", errText)
                onLog("[FATAL] $errText")
                return Result.failure(Exception(errText))
            }
            
            val successMsg = "Successfully authenticated Firebase Session for ${user.email} (UID: ${user.uid})"
            Log.i("GoogleAuth", successMsg)
            onLog("[SUCCESS] $successMsg")
            Result.success(user)
        } catch (e: GetCredentialException) {
            val errMsg = "Google account picker failed: ${e.javaClass.simpleName} — ${e.message}"
            Log.e("GoogleAuth", errMsg, e)
            onLog("[FATAL] $errMsg")
            onLog("[TRACE] ${Log.getStackTraceString(e)}")
            Result.failure(Exception(errMsg))
        } catch (e: GoogleIdTokenParsingException) {
            val errMsg = "Could not parse Google ID token: ${e.message}"
            Log.e("GoogleAuth", errMsg, e)
            onLog("[FATAL] $errMsg")
            onLog("[TRACE] ${Log.getStackTraceString(e)}")
            Result.failure(Exception(errMsg))
        } catch (e: Exception) {
            // This is the branch that fires when the picker succeeded but Firebase itself
            // rejected the token — e.g. "auth/invalid-credential", "auth/operation-not-allowed"
            // (Google provider not enabled in Firebase Console), or a client ID mismatch.
            val errMsg = "Firebase sign-in failed: ${e.javaClass.simpleName} — ${e.message}"
            Log.e("GoogleAuth", errMsg, e)
            onLog("[FATAL] $errMsg")
            onLog("[TRACE] ${Log.getStackTraceString(e)}")
            Result.failure(Exception(errMsg))
        } finally {
            Log.d("GoogleAuth", "--- END GOOGLE SIGN IN TRACE ---")
        }
    }

    suspend fun signOut(context: Context) {
        try {
            FirebaseAuth.getInstance().signOut()
        } catch (_: Exception) {}
        try {
            val credentialManager = CredentialManager.create(context)
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
        } catch (_: Exception) {
            // Non-fatal: worst case the account picker shows a previously-used account next time.
        }
    }

    /** Restores a previous session on app relaunch, since Firebase Auth persists sign-in state. */
    fun currentUser(): FirebaseUser? {
        return try {
            FirebaseAuth.getInstance().currentUser
        } catch (e: Exception) {
            null
        }
    }

    private fun generateNonce(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
