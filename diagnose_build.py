#!/usr/bin/env python3
import sys
import os
import re
import subprocess

# ANSI styling helper
class Style:
    HEADER = '\033[95m'
    BLUE = '\033[94m'
    CYAN = '\033[96m'
    GREEN = '\033[92m'
    YELLOW = '\033[93m'
    RED = '\033[91m'
    BOLD = '\033[1m'
    UNDERLINE = '\033[4m'
    RESET = '\033[0m'

def print_header(title):
    print(f"\n{Style.BOLD}{Style.CYAN}{'=' * 60}{Style.RESET}")
    print(f"{Style.BOLD}{Style.HEADER} {title} {Style.RESET}")
    print(f"{Style.BOLD}{Style.CYAN}{'=' * 60}{Style.RESET}\n")

def analyze_log_content(content):
    errors_found = []
    
    # 1. AAPT2 / Resource Errors
    aapt_patterns = [
        r"AAPT:\s*(.*)",
        r"AAPT2 error:\s*(.*)",
        r"Resource\s+(\S+)\s+not found",
        r"failed linking file resources",
        r"invalid resource directory name",
        r"Duplicate resources"
    ]
    aapt_matches = []
    for pattern in aapt_patterns:
        matches = re.findall(pattern, content, re.IGNORECASE)
        if matches:
            aapt_matches.extend(matches)
            
    if aapt_matches:
        errors_found.append({
            "category": "AAPT2 / Android Resource Issues",
            "evidence": aapt_matches[:5], # top 5
            "fix_guide": [
                "Check recent changes to XML files in 'app/src/main/res/values/', 'layout/', etc.",
                "Ensure XML files are properly closed and do not contain special unescaped characters like '&' (use '&amp;') or quotes in string resources without escaping.",
                "Verify that all drawable, layout, and value resources have valid names (lowercase letters, numbers, and underscores only).",
                "Check for duplicate resource names across files or dependencies."
            ]
        })

    # 2. Dependency / Resolution Errors
    dep_patterns = [
        r"Could not resolve\s+(.*)",
        r"Could not find\s+(.*)",
        r"Dependency\s+(\S+)\s+failed",
        r"Failed to resolve:\s*(.*)"
    ]
    dep_matches = []
    for pattern in dep_patterns:
        matches = re.findall(pattern, content, re.IGNORECASE)
        if matches:
            dep_matches.extend(matches)
            
    if dep_matches:
        errors_found.append({
            "category": "Dependency Resolution / Gradle Dependency Issues",
            "evidence": dep_matches[:5],
            "fix_guide": [
                "Verify that the library group, artifact name, and version are correct in 'app/build.gradle.kts' and 'gradle/libs.versions.toml'.",
                "Ensure required repositories (like 'google()', 'mavenCentral()', or custom Maven URLs) are declared in settings.gradle.kts or build.gradle.kts.",
                "If using a Version Catalog, make sure kebab-case TOML names are converted to dot-notation in Kotlin DSL files (e.g., 'androidx-core-ktx' to 'libs.androidx.core.ktx').",
                "Check internet connectivity and clear build cache if necessary."
            ]
        })

    # 3. Keystore & Signature Errors
    sig_patterns = [
        r"keystore file\s+(\S+)\s+not found",
        r"Failed to read key\s+(.*)",
        r"Password verification failed",
        r"signingConfig\s+(.*)",
        r"signing\s+failed",
        r"Invalid keystore format",
        r"Keystore"
    ]
    sig_matches = []
    for pattern in sig_patterns:
        matches = re.findall(pattern, content, re.IGNORECASE)
        if matches:
            sig_matches.extend(matches)
            
    # Also search for standard JKS/Keystore keywords in logs near exceptions
    if "keystore" in content.lower() or "signing" in content.lower():
        lines = content.split('\n')
        for line in lines:
            if "keystore" in line.lower() or "signingconfig" in line.lower() or "sign" in line.lower():
                if "failed" in line.lower() or "error" in line.lower() or "exception" in line.lower() or "missing" in line.lower():
                    sig_matches.append(line.strip())

    if sig_matches:
        errors_found.append({
            "category": "Keystore / App Signing Issues",
            "evidence": sig_matches[:5],
            "fix_guide": [
                "Verify that the release keystore file exists at the path defined in your 'app/build.gradle.kts' signingConfigs block.",
                "Check if required environment variables (like 'KEYSTORE_PASSWORD', 'STORE_PASSWORD', 'KEY_PASSWORD') are populated in the AI Studio secrets/environment variables.",
                "Ensure that the keystore is not corrupted and is base64 decoded correctly if imported as an environment variable.",
                "Never modify 'debug.keystore' or sign configuration variables unless explicitly updating the production key."
            ]
        })

    # 4. Kotlin / Java Compilation Errors
    comp_patterns = [
        r"e:\s+file://(\S+):(\d+):(\d+):\s+(.*)",
        r"Compilation error\.\s*(.*)",
        r"Cannot find symbol\s*(.*)",
        r"unresolved reference:\s*(\S+)"
    ]
    comp_matches = []
    for pattern in comp_patterns:
        # Match multiline or single line compilation messages
        matches = re.findall(pattern, content)
        if matches:
            comp_matches.extend([str(m) for m in matches])

    # Let's also parse typical file compilation failures
    if not comp_matches:
        lines = content.split('\n')
        for line in lines:
            if ("e: file://" in line) or ("unresolved reference" in line.lower()) or ("compilation error" in line.lower()):
                comp_matches.append(line.strip())

    if comp_matches:
        errors_found.append({
            "category": "Kotlin / Java Code Compilation Issues",
            "evidence": comp_matches[:5],
            "fix_guide": [
                "Locate the file and line number shown in the compiler output and correct syntax errors.",
                "Verify import statements are correct and that the classes/variables are declared or imported correctly.",
                "Avoid using deprecated API signatures (e.g. use '.uppercase()' instead of '.toUpperCase()').",
                "Ensure any generated code or annotations (Room, KSP, etc.) compile correctly by running 'compile_applet'."
            ]
        })

    # 5. Manifest Merger Errors
    manifest_patterns = [
        r"Manifest merger failed",
        r"Conflict with\s+(.*)",
        r"Attribute\s+(\S+)\s+is also present"
    ]
    manifest_matches = []
    for pattern in manifest_patterns:
        matches = re.findall(pattern, content, re.IGNORECASE)
        if matches:
            manifest_matches.extend(matches)

    if manifest_matches:
        errors_found.append({
            "category": "AndroidManifest.xml / Manifest Merger Issues",
            "evidence": manifest_matches[:5],
            "fix_guide": [
                "Open 'app/src/main/AndroidManifest.xml' and inspect it for duplicate permissions, duplicate activity definitions, or mismatched tag properties.",
                "Ensure all activities, services, and receivers that use intent filters explicitly declare 'android:exported=\"true\"' or 'android:exported=\"false\"'.",
                "Look for version conflicts between dependencies that declare same attributes or permissions with conflicting configurations (use 'tools:replace' to resolve if needed)."
            ]
        })

    # 6. Memory & JVM Errors
    mem_patterns = [
        r"OutOfMemoryError",
        r"GC overhead limit exceeded",
        r"Java heap space",
        r"expelled"
    ]
    mem_matches = []
    for pattern in mem_patterns:
        matches = re.findall(pattern, content, re.IGNORECASE)
        if matches:
            mem_matches.extend(matches)

    if mem_matches:
        errors_found.append({
            "category": "JVM / Out of Memory (OOM) Issues",
            "evidence": mem_matches[:5],
            "fix_guide": [
                "Increase the maximum heap size in 'gradle.properties' (e.g., 'org.gradle.jvmargs=-Xmx2048m -XX:MaxMetaspaceSize=512m').",
                "Check for memory leaks or infinite build loops in any custom Gradle tasks or annotation processors.",
                "Avoid running massive parallel compilation tasks if RAM is highly constrained."
            ]
        })

    return errors_found

def main():
    print_header("Android Applet Release Build Diagnostics")
    
    log_file = None
    if len(sys.argv) > 1:
        log_file = sys.argv[1]
        print(f"{Style.BOLD}{Style.BLUE}[i] Log file specified:{Style.RESET} {log_file}")
    else:
        # Check if default build.log exists
        if os.path.exists("build.log"):
            log_file = "build.log"
            print(f"{Style.BOLD}{Style.BLUE}[i] No argument provided. Using existing build.log.{Style.RESET}")
        else:
            print(f"{Style.BOLD}{Style.YELLOW}[!] No log file specified or found. Let's trigger a Release Bundle build to diagnose!{Style.RESET}")
            print(f"{Style.BLUE}[i] Running: gradle :app:bundleRelease --no-daemon{Style.RESET}\n")
            
            # Run the build
            try:
                # We save output to build.log and analyze it
                with open("build.log", "w") as log_out:
                    process = subprocess.Popen(
                        ["gradle", ":app:bundleRelease", "--no-daemon"],
                        stdout=subprocess.PIPE,
                        stderr=subprocess.STDOUT,
                        text=True
                    )
                    
                    # Read live output and write to log file + screen
                    for line in process.stdout:
                        sys.stdout.write(line)
                        log_out.write(line)
                        
                    process.wait()
                    log_file = "build.log"
                    print(f"\n{Style.BOLD}{Style.GREEN}[+] Build process finished with exit code {process.returncode}{Style.RESET}")
            except Exception as e:
                print(f"{Style.BOLD}{Style.RED}[x] Failed to run Gradle release build: {e}{Style.RESET}")
                sys.exit(1)

    # Now read and analyze the log file
    if not log_file or not os.path.exists(log_file):
        print(f"{Style.BOLD}{Style.RED}[x] Error: Log file could not be read or does not exist.{Style.RESET}")
        sys.exit(1)

    try:
        with open(log_file, "r", encoding="utf-8", errors="ignore") as f:
            content = f.read()
    except Exception as e:
        print(f"{Style.BOLD}{Style.RED}[x] Error reading log file: {e}{Style.RESET}")
        sys.exit(1)

    print(f"\n{Style.BOLD}{Style.CYAN}Analyzing logs from {log_file}...{Style.RESET}")
    diagnostics = analyze_log_content(content)
    
    if not diagnostics:
        # Check if build was successful
        if "BUILD SUCCESSFUL" in content:
            print(f"\n{Style.BOLD}{Style.GREEN}🎉 SUCCESS: The release build completed successfully! No issues detected.{Style.RESET}")
        else:
            print(f"\n{Style.BOLD}{Style.YELLOW}⚠️ WARNING: The build failed, but no specific known patterns were detected.{Style.RESET}")
            print(f"Please review the logs manually for any unexpected errors.")
    else:
        print(f"\n{Style.BOLD}{Style.RED}❌ DETECTED ISSUES ({len(diagnostics)} Categories Found):{Style.RESET}")
        for idx, issue in enumerate(diagnostics, 1):
            print(f"\n{Style.BOLD}{Style.RED}{idx}. Category: {issue['category']}{Style.RESET}")
            print(f"   {Style.BOLD}Evidence/Log Lines Found:{Style.RESET}")
            for line in issue['evidence']:
                print(f"     - {Style.YELLOW}{line.strip()}{Style.RESET}")
            print(f"   {Style.BOLD}{Style.GREEN}Recommended Action Checklist:{Style.RESET}")
            for step in issue['fix_guide']:
                print(f"     [ ] {step}")
                
    print(f"\n{Style.BOLD}{Style.CYAN}{'=' * 60}{Style.RESET}\n")

if __name__ == "__main__":
    main()
