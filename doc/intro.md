# DUL Authority Tool



## Workflows

Key to diagrams:

 - [M] - Manual task
 - [P] - Task uses DUL Authority Tool.
 - [T] - Task uses second / third party tool.

### Task 1: Producer wants to Register 

![Task 1: Producer wants to Register ](sequence-diagram-1.png)

#### Description

#### Steps

 - 1.1 Producer: Send email with proof of identity.
 - 1.2 Authority Contact: Verify Identity of sender.
 - 1.3 Authority Contact: Create new Producer ID and verify it's unique.
 - 1.4 Authority Contact: Add Producer ID to registry.
 - 1.5 Authority Contact: Send confirmation with Producer ID.

#### Failure conditions

 - 1.2 Authority Contact is unable to verify identity of Producer.
     - Authority should contact Producer to verify by other means. 
     - If not possible, verification cannot continue.

### Task 2: Producer wants to send Certificate

![Task 2: Producer wants to send Certificate ](sequence-diagram-2.png)

#### Steps

 - 2.1 Producer: Create certificate using own tool
     - Format of certificate should be JWK using RSA256 algorithm
 - 2.2 Producer: Send email with proof of identity and new certificate 
 - 2.3 Authority Contact: Verify identity of sender
 - 2.4 Authority Contact: Verify certificate format
 - 2.5 Authority Contact: Add certificate to registry
     - Registry Tool should assign an ID to the key
     - A JWK JKU URL should be returned.
 - 2.6 Producer: Send confirmation with Producer ID
     - The JKU should be sent back to the Producer for inclusion in future messages.

#### Failure Conditions

 - 2.3 Authority Contact is unable to verify identity of Producer
     - Authority should contact Producer vo verify by other means.
     - If not possible, process cannot continue.
 - 2.4 Authority Contact is unable to verify format of certificate
     - If certificate is not JWK 256 then it should be rejected.

### Task 3: Consumer wants info on Producer

![Task 3: Consumer wants info on Producer](sequence-diagram-3.png)

 - 3.1 Consumer receives message from Producer with Producer ID.
 - 3.2 Consumer queries Producer Registry for information for Producer ID.
 - 3.3 Producer Registry sends back information about Producer.


### Task 4: Consumer wants to verify Producer key

![Task 4: Consumer wants to verify Producer key](sequence-diagram-4.png)

 - 4.1 Consumer receives message from Producer with Producer ID and Certificate Key.
 - 4.2 Consumer queries Producer Registry for information for Producer ID.
 - 4.3 Producer Registry sends back information about Producer.
 - 4.4 Consumer queries Producer Registry for information for Producer ID.
 - 4.5 Producer Registry sends back information about Producer.

## Tool
 
### List Producers
 
Return a list of Producer IDs and associated metadata.
 
    lein run list
 
### Add Producer
 
Add a new Producer and associated metadata.
 
     lein run add «producer-id»

#### Failure

 - Will fail if the Producer ID already exists.
 
### Validate Certificate
 
Validate the format and contents of a certificate.

    lein run validate «path-to-certificate»
    
#### Success

 - Will return "OK" if the certificate validates.

#### Failure

 - Will return "Error: Cannot read" if the certificate cannot be read because it is corrupted or the wrong format.
 - Will return "Error: Private Key included" if the certificate doesn't contain ONLY the public key.
 - Will return "Error: Wrong algorithm" if the algorithm is not RS256 
 
### Upload Certificate
 
Upload a certificate to the store.

    lein run upload «producer-id» «path-to-certificate»

#### Success

If the certificate is uploaded correctly, it will return "OK".
 
#### Failure

 - Will return error codes from `validate` if the certificate isn't valid.
 - Will return error if Produce ID doesn't exist.
 - Will return "Error: Can't connect to bucket" if it's not possible to upload the key.

