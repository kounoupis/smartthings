# smartthings
DTH for the GE/Jasco Z-Wave Plus On/Off Switches that can execute Scenes.

Installation:
1. Log onto the SmartThings IDE
2. Select "My Device Handlers"
3. Click "Settings" and select "Add new repository"
    - Owner "kounoupis", Name "SmartThings", Branch "master"
4. Add you Smarthings rest API oauth key in line 282:
    - state.oauth_token = "\<Replace with smartthings oauth key\>"
5. You can assign scenes for double-tap up/down using scene ids. To find your scene ids follow the instructions here:
  `https://smartthings.developer.samsung.com/docs/devices/working-with-scenes.html`
