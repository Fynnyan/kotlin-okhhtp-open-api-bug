
openapi generator, generate client with `kotlin` `okhttp4` and `jackson`

run ``mvn test``, there is a test that will print our the send request and response 

multipart/form-data, object part are not serialized to json

affected versions of the open api generator
- 6.4.0 
- 6.6.0
- 7.0.0


the generated code does a plain to string operation on objects in a multipart/form-data request
when it should deserialize it to json

```kotlin
/*
 * Part of the generated code "[ApiClient.kt]
 */
protected inline fun <reified T> requestBody(content: T, mediaType: String?): RequestBody =
    //...
    mediaType == FormDataMediaType ->
    MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .apply {
            (content as Map<String, PartConfig<*>>).forEach { (name, part) ->
                if (part.body is File) {
                    // do file stuff
                } else {
                    // all else do a toString on objects that are not collections of some type of Date / DateTime 
                    val partHeaders = part.headers.toMutableMap() +
                            ("Content-Disposition" to "form-data; name=\"$name\"")
                    addPart(
                        partHeaders.toHeaders(),
                        parameterToString(part.body).toRequestBody(null)
                    )
                }
            }
        }
```

payload:
```
Content-Disposition: form-data; name="metadata"
Content-Length: 39
Metadata(email=john@doe.com, locale=en)
--4975a2aa-2415-4a44-9231-a6c74e59088a
Content-Disposition: form-data; name="report"; filename="report-5cc1c8cf-9780-4268-b0dd-cc57c1b7a45c270820599018655007.xml"
Content-Type: application/xml
Content-Length: 83

<?xml version="1.0" encoding="UTF-8" ?>
<body>
    <value>Some Text</value>
</body>
--4975a2aa-2415-4a44-9231-a6c74e59088a--
```

expected payload
```
Content-Disposition: form-data; name="metadata"
Content-Type: application/json

{ "email": "john@doe.com" "locale": "en" }
--4975a2aa-2415-4a44-9231-a6c74e59088a
Content-Disposition: form-data; name="report"; filename="report-5cc1c8cf-9780-4268-b0dd-cc57c1b7a45c270820599018655007.xml"
Content-Type: application/xml
Content-Length: 83

<?xml version="1.0" encoding="UTF-8" ?>
<body>
    <value>Some Text</value>
</body>
--4975a2aa-2415-4a44-9231-a6c74e59088a--
```