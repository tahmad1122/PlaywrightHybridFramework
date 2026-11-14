pipeline {
    agent any

    options {
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
    }

    triggers {
        pollSCM('H/2 * * * *')      // har ~2 minute mein naya push check karo
    }

    environment {
        GROQ_API_KEY      = credentials('groq-api-key')
        SLACK_WEBHOOK_URL = credentials('slack-webhook-url')
    }

    stages {
        stage('Checkout') {
            steps { checkout scm }
        }

        stage('Run Tests') {
            steps {
                bat 'if exist test-output\\ai-reports rmdir /s /q test-output\\ai-reports'
                bat 'mvn -B clean test -Dheadless=true -DsuiteFile=testng.xml'
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: 'reports/**, test-output/**, logs/**',
                             allowEmptyArchive: true

            powershell '''
$path = "test-output/ai-reports/ai-summary.json"
if (-not (Test-Path $path)) {
    $text = "*QA Build #$env:BUILD_NUMBER* - ai-summary.json not found (build failed before tests finished). $env:BUILD_URL"
} else {
    $d = Get-Content $path -Raw | ConvertFrom-Json
    $status = if ($d.failed -gt 0) { "FAILED" } else { "PASSED" }
    $lines = @(
        "*QA Build #$env:BUILD_NUMBER - $status*",
        "Total: $($d.total) | Passed: $($d.passed) | Failed: $($d.failed) | Skipped: $($d.skipped)"
    )
    if ($d.failedTests) { $lines += "Failed tests: $($d.failedTests)" }
    $lines += ""
    $lines += "*AI analysis:*"
    $lines += $d.aiSummary
    $lines += ""
    $lines += "Build: $env:BUILD_URL"
    $text = $lines -join [Environment]::NewLine
}
$body = @{ text = $text } | ConvertTo-Json
Invoke-RestMethod -Uri $env:SLACK_WEBHOOK_URL -Method Post `
    -Body ([System.Text.Encoding]::UTF8.GetBytes($body)) `
    -ContentType "application/json; charset=utf-8"
'''
        }
    }
}