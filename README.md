Made this to learn java. If anything needs fixing, make a pull request

**Building**
- gradlew.bat setupDecompWorkspace
- gradlew.bat build
  
**Features**
- HWID Protection
- Login System which requires user to provide username and pass. Credentials are stored encrypted in an pastebin
- KillSwitch: Ability to close client. Works by reading a string from a pastebin. If string equals "true", server closes, if "false" (can be everything else but "true") then client doesnt close
- Anti Dump
- Anti VM
- Checks if the authentication is valid or not from the loaded client

**Credits**
- https://github.com/x4e/falcon-forge
