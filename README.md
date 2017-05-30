# Crossref Distributed Usage Logging Authority Tool

Tool for maintaining the Authority Registry in Crossref Distributed Logging (DUL) framework.

## Configuration

| Environment variable         | Description                         |
|------------------------------|-------------------------------------|
| `S3_KEY`                     | AWS Key Id                          |
| `S3_SECRET`                  | AWS Secret Key                      |
| `S3_BUCKET_NAME`             | AWS S3 bucket name                  |
| `S3_REGION_NAME`             | AWS S3 bucket region name           |
| `PUBLIC_BASE`                | Public URL base of server           |
| `CLOUDFRONT_DISTRIBUTION_ID` | AWS Cloudfront Distribution ID      |

`PUBLIC_BASE` should be base URL from which files are served, for example `https://dul.eventdata.crossref.org`. It is used when constructing URLs in indexes.
