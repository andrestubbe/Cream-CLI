Set shell = CreateObject("WScript.Shell")
Dim filePath
If WScript.Arguments.Count > 0 Then
    filePath = WScript.Arguments(0)
Else
    filePath = ""
End If

Dim batPath
batPath = "C:\Users\andre\Documents\2026-07-16-Work-Cream\2026-07-16-Work-CreamCLI\cream.bat"

Dim cmd
If filePath <> "" Then
    cmd = "cmd.exe /c """ & batPath & """ """ & filePath & """"
Else
    cmd = "cmd.exe /c """ & batPath & """"
End If

' 3 = SW_MAXIMIZE — window starts maximized from the very first frame
shell.Run cmd, 3, False
