# TorController

Simple project to tunnel your connections through tor network.

### Find tor executable

Download and extract tor from [here](https://www.torproject.org/projects/torbrowser.html.en).

__Linux__ executable should be here: `tor-browser_en-US/Browser/TorBrowser/Tor/tor`.

__Windows__ executable should be here: `Tor Browser\Browser\TorBrowser\Tor\tor.exe`

## Examples
The `TorController` has three simple methods:

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

### Tunnel Apache Http Client, JSoup or Selenium FirefoxDriver

Have a look at these [examples](https://github.com/al-eax/torcontroller/tree/master/examples)

### Set password and ports

The TorController uses 9150 and 9151 as default ports for socks and controll server. Set them in constructor if you want.
You can also specify a password to protect your local tor control server from other connections:

```java
int socks_port = 1337;
int control_port = 1338
String path = ".../tor-browser_en-US/Browser/TorBrowser/Tor/tor";
TorController tor = new TorController(path,socks_port,control_port);
tor.setPassword("Mb2.r5oHf-0t".toCharArray());
```
