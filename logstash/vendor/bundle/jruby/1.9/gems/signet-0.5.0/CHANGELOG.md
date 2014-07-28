# 0.4.5

* Minor documentation fixes
* Allow postmessage as a valid redirect_uri in OAuth 2

# 0.4.4

* Add support for assertion profile

# 0.4.3

* Added method to clear credentials

# 0.4.2

* Backwards compatibility for MultiJson

# 0.4.1

* Updated Launchy dependency

# 0.4.0

* Added OAuth 1 server implementation
* Updated Faraday dependency

# 0.3.4

* Attempts to auto-detect CA cert location

# 0.3.3

* Request objects no longer recreated during processing
* Faraday middleware now supported
* Streamed requests now supported
* Fixed assertion profiles; client ID/secret omission no longer an error

# 0.3.2

* Added audience security check for ID tokens

# 0.3.1

* Fixed a warning while determining grant type
* Removed requirement that a connection be supplied when authorizing requests
* Updated addressable dependency to avoid minor bug
* Fixed some documentation stuff around markdown formatting
* Added support for Google Code wiki format output when generating docs

# 0.3.0

* Replaced httpadapter gem dependency with faraday
* Replaced json gem dependency with multi_json
* Updated to OAuth 2.0 draft 22
* Complete test coverage

# 0.2.4

* Updated to incorporate changes to the Google OAuth endpoints

# 0.2.3

* Added support for JWT-formatted ID tokens.
* Added :issued_at option to #update_token! method.

# 0.2.2

* Lowered requirements for json gem

# 0.2.1

* Updated to keep in sync with the new httpadapter changes

# 0.2.0

* Added support for OAuth 2.0 draft 10

# 0.1.4

* Added support for a two-legged authorization flow

# 0.1.3

* Fixed issue with headers passed in as a Hash
* Fixed incompatibilities with Ruby 1.8.6

# 0.1.2

* Fixed bug with overzealous normalization

# 0.1.1

* Fixed bug with missing StringIO require
* Fixed issue with dependency on unreleased features of addressable

# 0.1.0

* Initial release
