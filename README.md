**Code Explainer — IntelliJ Plugin**

An IntelliJ IDEA plugin that explains selected code using the Claude AI API. Choose your experience level — Junior, Mid, or Senior — and get a clear, targeted explanation right inside the IDE.


**Demo**
- Select any code in the editor, press Ctrl+Shift+E, and the explanation appears instantly in the side panel.
<img width="1395" height="676" alt="image" src="https://github.com/user-attachments/assets/d7b0becb-7dda-4e65-9148-2192d6a6b721" />

**Features**
- Three explanation levels — Junior, Mid, Senior. The prompt adapts so the explanation matches your background.
- Inline side panel — results appear in a docked tool window, no popups or dialogs.
- Right-click — on the editor context menu.
- Secure API key storage — key is saved with IntelliJ's PropertiesComponent, never hardcoded.
- Background HTTP call — the UI stays responsive while Claude processes the request.

**How It Works**
User selects code in editor
        ↓
Right-click → Explain Code
        ↓
ExplainAction reads the selection via SelectionModel
        ↓
HTTP POST to api.anthropic.com/v1/messages
runs on a background thread (executeOnPooledThread)
        ↓
Response displayed in the Tool Window
UI update runs on EDT (invokeLater)

<img width="1398" height="834" alt="image" src="https://github.com/user-attachments/assets/dd12543f-f1b2-4270-8e40-66ee0ca429d7" />

**If no code selected:**<img width="1368" height="796" alt="image" src="https://github.com/user-attachments/assets/5db5bb0c-66e9-4679-a7a5-169ff06262ec" />

**Explained text:**<img width="1188" height="723" alt="image" src="https://github.com/user-attachments/assets/ebfb997d-979c-40be-a23b-938feb8ad3f8" />

**Project Structure**
src/main/java/com/example/codeexplainer/

-     ├── actions/
-     │   └── ExplainAction.java           # Reads editor selection, triggers the flow
-     ├── client/
-     │   └── ClaudeClient.java            # HTTP call to Claude API (OkHttp + Gson)
-     ├── settings/
-     │   ├── AppSettings.java             # Stores API key via PropertiesComponent
-     │   └── AppSettingsConfigurable.java # Settings page under Preferences → Tools
-     └── toolwindow/
-     └── ExplainToolWindowFactory.java # Side panel UI (level selector + result area)

-     src/main/resources/META-INF/
-     └── plugin.xml                       # Plugin registration: action, toolwindow, settings


****Getting Started****

**Prerequisites**
- IntelliJ IDEA 2023.1+ (Community or Ultimate)
- JDK 21
- An Anthropic API key

**Run locally**
bashgit clone https://github.com/Dasa-lu/code-explainer.git
cd code-explainer
./gradlew runIde

- A sandboxed IntelliJ instance launches with the plugin installed.
- Set your API key

Open Preferences → Tools → Code Explainer
Paste your Anthropic API key
<img width="1392" height="814" alt="image" src="https://github.com/user-attachments/assets/4d1d49ac-f641-4160-bfb8-7cca98a509ea" />

**Usage**
- Run ./gradlew runIde
- Open any project in the sandboxed IntelliJ instance
- Select a code snippet in the editor
- Press Ctrl+Shift+E (or right-click → Explain Code)
- Pick a level in the Code Explainer panel: Junior / Mid / Senior
- Read the explanation in the side panel


**Design Decisions**
- Threading — The Claude API call runs on executeOnPooledThread so the IDE never freezes. The UI update is dispatched back to the EDT via invokeLater, following IntelliJ's threading model.
- API key storage — PropertiesComponent.getInstance() persists the key in IntelliJ's own settings storage. It survives restarts and is never written to source files.
- Static panel reference — ExplainToolWindowFactory holds a static currentPanel so ExplainAction can push text into the tool window without a complex service layer. This is a deliberate simplification: it works correctly for single-project use.
