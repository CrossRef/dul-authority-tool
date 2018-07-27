# Crossref Distributed Usage Logging Authority Tool

Tool for maintaining the Authority Registry in Crossref Distributed Logging (DUL) framework. Full data flow described in [doc/intro.md](doc/intro.md)

## Tool
 
### List Producers
 
Return a list of Producer IDs and associated metadata.
 
    lein run list
 
### Add Producer
 
Add a new Producer and associated metadata.
 
     lein run add «producer-id»

 - OK on success
 - Will fail if the Producer ID already exists.
 
### Validate Certificate
 
Validate the format and contents of a certificate.

    lein run validate «path-to-certificate»
    
 - Will return "OK" if the certificate validates.
 - Will return "Error" if the certificate cannot be read because it is corrupted or the wrong format.
 - Will return "Error" if the certificate doesn't contain ONLY the public key.
 - Will return "Error" if the algorithm is not RS256 
 
### Upload Certificate
 
Upload a certificate to the store.

    lein run upload «producer-id» «path-to-certificate»

 - On success will return a JWK URL which should be sent back to the Producer so they can use it.
 - Will return "Error" if Producer ID is not registered.
 - Will return error codes from `validate` if the certificate isn't valid.
 - Will return "Error: Can't connect to bucket" if it's not possible to upload the key.


## Configuration

| Environment variable         | Description                                      |
|------------------------------|--------------------------------------------------|
| `S3_KEY`                     | AWS Key Id                                       |
| `S3_SECRET`                  | AWS Secret Key                                   |
| `S3_BUCKET_NAME`             | AWS S3 bucket name                               |
| `S3_REGION_NAME`             | AWS S3 bucket region name                        |
| `PUBLIC_BASE`                | Public URL base of server without trailing slash |
| `CLOUDFRONT_DISTRIBUTION_ID` | AWS Cloudfront Distribution ID                   |
| `TEST`                       | 'true' when running tests                        |

## Testing 

    ./test.sh


`PUBLIC_BASE` should be base URL from which files are served, for example `https://dul-token.crossref.org`. It is used when constructing URLs in indexes.
