package com.example.auth

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import java.security.SecureRandom

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
 */
object GoogleAuthManager {

    // From google-services.json -> client[0].oauth_client[] where client_type == 3
    private const val WEB_CLIENT_ID =
        "861274979266-fji7kfo11r23ihjhlq5blsh2903lvuj3.apps.googleusercontent.com"

    suspend fun signIn(context: Context): Result<FirebaseUser> {
        return try {
            val credentialManager = CredentialManager.create(context)

            val signInOption = GetSignInWithGoogleOption.Builder(WEB_CLIENT_ID)
                .setNonce(generateNonce())
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(signInOption)
                .build()

            val result = credentialManager.getCredential(context, request)
            val credential = result.credential

            if (credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                return Result.failure(Exception("Unexpected credential type returned"))
            }

            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val firebaseCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)

            val authResult = FirebaseAuth.getInstance().signInWithCredential(firebaseCredential).await()
            val user = authResult.user ?: return Result.failure(Exception("Sign-in succeeded but no user was returned"))
            Result.success(user)
        } catch (e: GetCredentialException) {
            Result.failure(Exception("Google Sign-In was cancelled or failed: ${e.message}"))
        } catch (e: GoogleIdTokenParsingException) {
            Result.failure(Exception("Could not parse Google ID token: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
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
