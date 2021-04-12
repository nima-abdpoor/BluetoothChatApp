# BluetoothChatApp

## OVERVIEW
<img src="https://github.com/nima-abdpoor/BluetoothChatApp/blob/master/screenShots/devices.jpg" width="270">.<img src="https://github.com/nima-abdpoor/BluetoothChatApp/blob/master/screenShots/chat.jpg" width="270">

## DEVELOPMENT ROADMAP
<img src="https://github.com/nima-abdpoor/BluetoothChatApp/blob/master/screenShots/Tasks.PNG">

### sockets
1.first i learned how to work with bluetooth so i refered to [this](https://developer.android.com/guide/topics/connectivity/bluetooth) and i got all things about Bluetooth.
check Bluetooth adapter, check for permissions, get Paired devices, create Connect socket, Create Accept socket, send message
but socket  get an exception when a message sent after hours I found [BluetoothService](https://github.com/googlearchive/android-BluetoothChat/blob/master/Application/src/main/java/com/example/android/bluetoothchat/BluetoothChatService.java) that handles all Accept and Connect Threads, so I paste it in my project.

### chat structure
2.I created the structure of the whole chat. 
the Message class is an interface that holds all kinds of messages, so each message(Text, file,...) should implement the Message class. each Message has content that should override its own content and has child messages and its own father(if exists).

### reliability
3.each message has sent has extra data: 1-a random integer between 1000 and 9999 that should make each message unique(UID) 2- an integer that shows the status( 0 is none, 1 is sent and 2 is seen) 3- an 0 or 1 that shows isMe or not.
the status of the first message that has been sent is called SENT, the receiver gets it and decodes, store and send it back with status SEEN.
an application that gets a message should check the status of the message firstly if the status is SEEN so it's my own message that gets back and should update database and UI and if the status is SENT the application should save it in the database and change the status and get it back.

### UI
4.get all messages from the database with all status and show theme in ChatFragment.kt.I handled this process with Flow instead of LiveData.
store devices that users want to connect and show them on the first page(ChatListFragement.kt).
