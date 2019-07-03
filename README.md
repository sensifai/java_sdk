Sensifai API Java Client
====================


## Overview
This Java client provides a wrapper around Sensifai [Image and Video recognition API](https://developer.sensifai.com).

## Installation
The API client is available on both Maven and Gradle .
Using this API is pretty simple.
First, you need to install it using `maven` or `gradle` like this:

- Maven:
```bash
mvn install 
```
or directly use in your `pom.xml` file:
```xml
<dependency>
		<groupId>com.sensifai.java</groupId>
		<artifactId>SDK</artifactId>
		<version>1.0.3</version>
</dependency>
```

- Gradle:
```groovy
implementation 'com.sensifai.java:SDK:1.0.3'
```

Then, you can import it and use it as follows:

```java
import com.sensifai.java.sdk.SensifaiApi;

public static void main() {
	SensifaiApi api = new SensifaiApi("YOUR_APPLICATION_TOKEN");
}
```


### Sample Usage
The following example will set up the client and predict video or image attributes.
First of all, you need to import the library and define an instance as mentioned above.
You can get a free limited `token` from [Developer Panel](https://developer.sensifai.com) by creating an application.
Then, if you want to process Data by URL you can call `uploadByUrl` like the below sample code.

```java
ArrayList<String> urls = new ArrayList<>();
urls.add("https://test.jpg");
urls.add("https://test.png");
JSONObject jsonObject = api.uploadByUrl(urls);
```

Also, if you want to process Data by File, you can call `uploadByFile` like the following sample code. 

```java
ArrayList<File> files = new ArrayList<>();
File first = new File("test.jpg");
File second = new File("test.png");
files.add(first);
files.add(second);
JSONObject jsonObject = api.uploadByFile(files);
```

In the end, to retrieve the result of a task, pass its taskID through `getResult`.
Please don't forget to pass a single `TaskID`! this function won't work with a list of taskIDs.

```java
JSONObject jsonObject = api.getResult("XXXX-XXX-XXXX-XXXX");
```

### Full Code

```java
import com.sensifai.java.sdk.SensifaiApi;

public static void main() {
	SensifaiApi api = new SensifaiApi("YOUR_APPLICATION_TOKEN");
	ArrayList<String> urls = new ArrayList<>();
	urls.add("https://test.jpg");
	urls.add("https://test.png");
	JSONObject jsonObject = api.uploadByUrl(urls);
	// in case of send file
	// ArrayList<File> files = new ArrayList<>();
	// File first = new File("test.jpg");
	// File second = new File("test.png");
	// files.add(first);
	// files.add(second);
	// JSONObject jsonObject = api.uploadByFile(files);
	JSONObject jsonObject = api.getResult()
}
```
