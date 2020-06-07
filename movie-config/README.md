# Movie Config
The Movie Config component provides the configuration for the Movie Recommender Service. For this [Spring Cloud Config](https://cloud.spring.io/spring-cloud-config/reference/html/) is used.

## Configuration storage
For simplicity it is configured to use the local file system to read the configuration files. These files can be found in the config subdirectory.

## Protecting sensitive configuration
Asymmetric encryption is used to protect sensitive configuration such as API keys. To achieve this a private and a public key have been generated. The public key is placed in a self-signed certificate. This certificate and private key are stored in a JKS keystore. The public key is then used to encrypt the API key for TasteDive. The encrypted property is included in the configuration file.

## Client side decryption
To prevent that the Movie Config component must be in possession of the private key to decrypt encrypted properties we are using client side decryption. This implies that the Movie Config component does not decrypt encrypted properties. Instead it returns the encrypted properties to the Movie Recommender Service instead, where the decryption takes place. The advantage of this is that only the Movie Recommender Service needs to have access to the key material, instead of having to distribute this to multiple components.
