# TorController
Simple project to tunnel your connections through tor network.

__find tor executable:__

Download and extract tor from [this link](https://www.torproject.org/projects/torbrowser.html.en) to a direcroty.

__Linux__ users should find the tor executable here: `tor-browser_en-US/Browser/TorBrowser/Tor/tor`.

__Windows__ users should find it here: `Tor Browser\Browser\TorBrowser\Tor\tor.exe`

## Examples
The class `TorController` has three simple methods:

1. `startUp()` to start a tor process and connect to the control server

2. `changeIdentity()` to change your identity (ip)

3. `shutDown()` to disconnect from control server and terminate the tor process

### Minimal example
```java
String executable = "tor-browser_en-US/Browser/TorBrowser/Tor/tor";
TorController tor = new TorController(executable);
if(tor.startUp()){
  //...
  if(tor.changeIdentity()){
    //...
  }
  if(tor.shutDown()){
    //...
  }
}
```

### Tunnel UrlConnection
```java
String url = "http://api.ipify.org/?format=text";
TorController tor = new TorController(...);
SocketAddress proxyAddr = new InetSocketAddress("127.0.0.1", tor.getSocksPort());
Proxy proxy = new Proxy(Proxy.Type.SOCKS, proxyAddr);
URLConnection connection = new URL(url).openConnection(proxy);
BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));

String line;
while ((line = rd.readLine()) != null) {
  System.out.println(line);
}
rd.close();
```

### Tunnel Apache Http Client, JSoup and Selenium FirefoxDriver
Have a look at these [examples](https://github.com/al-eax/torcontroller/tree/master/examples)   
