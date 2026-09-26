# AI Agent Android APK

This is a clean Android foundation for the Level 109 AI Agent.

Included:
- Agent UI
- Calculator tool
- Voice input
- Text-to-speech
- Safe placeholder for Gemini/backend integration
- GitHub Actions workflow that builds the APK automatically

## Build without Android Studio

1. Create a GitHub repository.
2. Upload this project.
3. Open Actions.
4. Select "Build AI Agent APK".
5. Run the workflow.
6. Download the `AI-Agent-debug-apk` artifact.

## Security

Do NOT put a Gemini/OpenAI/Twilio secret key directly inside the APK.
Use a secure backend for production API calls.

Phone calling is not enabled by this starter APK.
