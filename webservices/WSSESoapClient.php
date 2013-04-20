<?php
/*********************************************************************************
 * The contents of this file are subject to the Common Public Attribution
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.openemm.org/cpal1.html. The License is based on the Mozilla
 * Public License Version 1.1 but Sections 14 and 15 have been added to cover
 * use of software over a computer network and provide for limited attribution
 * for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenEMM.
 * The Original Developer is the Initial Developer.
 * The Initial Developer of the Original Code is AGNITAS AG. All portions of
 * the code written by AGNITAS AG are Copyright (c) 2007 AGNITAS AG. All Rights
 * Reserved.
 * 
 * Contributor(s): AGNITAS AG. 
 ********************************************************************************/

  /*
    SOAP client with WSSE support for use of EMM/OpenEMM Webservice API 2.0
  */
class WsseSoapClient extends SoapClient {

  /* Namespace for SOAP */
  const SOAP_NAMESPACE = 'http://schemas.xmlsoap.org/soap/envelope/';

  /* Namespace for WSSE */
  const WSSE_NAMESPACE = 'http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd';

  /* Namespace for WSU*/
  const WSU_NAMESPACE = 'http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd';

  /* Encoding of "nonce" */
  const NONCE_ENCODING_TYPE = 'http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary';

  /* Type of password */
  const PASSWORD_TYPE = 'http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordDigest';

  /* Username for authentication */
  var $username;

  /* Password for authentication */
  var $password;

  /* Prefix used for nonce generation */
  var $prefix;

  /*
    Creates a new SOAP client with WSSE support.

    Parameter:
    $wsdlUrl:      location of WSDL document
    $username:     username for authentication
    $password:     password
    $options:      optional configuration information (see SoapClient in PHP manual)
    $prefix:       optional prefix used for nonce-generation.

    The prefix is used, when client runs on different machines. To prevent generation of the same nonce when run at the same time,
    a different prefix should be used for different instances of the client.
  */
  function __construct( $wsdlUrl, $username, $password, $prefix = null, $options = array()) {
    parent::__construct( $wsdlUrl, $options);

    $this->username = $username;
    $this->password = $password;

    $this->prefix = $prefix == null ? gethostname() : $prefix;
  }

  /*
    Implementation of "SoapClient::__doRequest" to add the WSSE security informations automatically.
  */
  function __doRequest( $request, $location, $action, $version) {
    $nonce = $this->generateNonce( $this->prefix);
    $timestamp = $this->getUTCTimestamp();
    $passwordDigest = $this->generatePasswordDigest( $this->password, $timestamp, $nonce);

    $xml = new DOMDocument();
    $xml->loadXML( $request);
    $this->appendWsseSecurityElements( $xml);
    $request = $xml->saveXML();

    echo "REQUET".$request;

    return parent::__doRequest( $request, $location, $action, $version);
  }

  /*
     Appends the WSSE security information to the SOAP header.

     Parameter:
     $xml:    the parsed SOAP request
  */
  private function appendWsseSecurityElements( $xml) {
    $nonce = $this->generateNonce( $this->prefix);
    $timestamp = $this->getUTCTimestamp();
    $passwordDigest = $this->generatePasswordDigest( $this->password, $timestamp, $nonce);

    $headerElement = $this->getSoapHeaderElement( $xml);
    $headerElement->appendChild( $this->createSecurityElement( $xml, $passwordDigest, $nonce, $timestamp));
  }

  /*
    Locates and returns the SOAP Header element.
    If Header element is not present, a new Header element is added to the SOAP request.

    Parameter:
    $xml:    the parsed SOAP request

    Returns:
    XML SOAP "Header" element
  */
  private function getSoapHeaderElement( $xml) {

    $xpath = new DOMXPath( $xml);
    $xpath->registerNamespace( 'SOAP-ENV', self::SOAP_NAMESPACE);

    $headerElement = $xpath->query('/SOAP-ENV:Envelope/SOAP-ENV:Header')->item(0);

    if( !$headerElement) {
      $headerElement = $xml->createElementNS( self::SOAP_NAMESPACE, 'SOAP-ENV:Header');
      $envelopeElement = $xpath->query('/SOAP-ENV:Envelope')->item(0);
      $bodyElement =$xpath->query('/SOAP-ENV:Envelope/SOAP-ENV:Body')->item(0);
      $envelopeElement->insertBefore( $headerElement, $bodyElement);
    }

    return $headerElement;
  }

  /*
    Creates the Security element for WSSE.

    Parameter:
    $xml:               the parsed SOAP request
    $passwordDigest:    the computed password digest
    $nonce:             nonce used for password digest
    $timestamp:         timestamp of digest generation

    Returns:
    XML "Security" element
  */
  private function createSecurityElement( $xml, $passwordDigest, $nonce, $timestamp) {
    $securityElement = $xml->createElementNS( self::WSSE_NAMESPACE, 'wsse:Security');
    $securityElement->appendChild( $this->createUsernameTokenElement( $xml, $passwordDigest, $nonce, $timestamp));

    return $securityElement;
   }

  /*
    Creates the UsernameToken element.

    Parameter:
    $xml:               the parsed SOAP request
    $passwordDigest:    the computed password digest
    $nonce:             nonce used for password digest
    $timestamp:         timestamp of digest generation

    Returns:
    XML "UsernameToken" element
  */
  private function createUsernameTokenElement( $xml, $passwordDigest, $nonce, $timestamp) {
    $usernameTokenElement = $xml->createElementNS( self::WSSE_NAMESPACE, 'wsse:UsernameToken');

    $usernameTokenElement->appendChild( $this->createUsernameElement( $xml));
    $usernameTokenElement->appendChild( $this->createPasswordElement( $xml, $passwordDigest));
    $usernameTokenElement->appendChild( $this->createNonceElement( $xml, $nonce));
    $usernameTokenElement->appendChild( $this->createCreatedElement( $xml, $timestamp));

    return $usernameTokenElement;
  }

  /*
    Creates the Username element.

    Parameter:
    $xml:    parsed SOAP request

    Returns:
    XML "Username" element
  */
  private function createUsernameElement( $xml) {
    return $xml->createElementNS( self::WSSE_NAMESPACE, 'wsse:Username', $this->username);
  }

  /*
    Creates the Password element.

    Parameter:
    $xml:              parsed SOAP request
    $passwordDigest:   password digest

    Returns:
    XML "Password" element
  */
  private function createPasswordElement( $xml, $passwordDigest) {
    $passwordElement = $xml->createElementNS( self::WSSE_NAMESPACE, 'wsse:Password', $passwordDigest);
    $passwordElement->setAttribute( 'Type', self::PASSWORD_TYPE);

    return $passwordElement;
  }

  /*
    Creates the Created element.

    Parameter:
    $xml:              parsed SOAP request
    $timestamp:        timestamp of digest generation

    Returns:
    XML "Created" element
  */
  private function createCreatedElement( $xml, $timestamp) {
    return $xml->createElementNS( self::WSU_NAMESPACE, 'wsu:Created', $timestamp);
  }

  /*
    Creates the Nonce element.

    Parameter:
    $xml:    parsed SOAP request
    $nonce:  nonce used for digest generation

    Returns:
    XML "Nonce" element
  */
  private function createNonceElement( $xml, $nonce) {
    $nonceElement = $xml->createElementNS( self::WSSE_NAMESPACE, 'wsse:Nonce', $nonce);
    $nonceElement->setAttribute( 'EncodingType', self::NONCE_ENCODING_TYPE);

    return $nonceElement;
  }

  /*
    Generates password digest

    Parameter:
    $password   plain-text password
    $timestamp  timestamp of generation
    $nonce      nonce value

    Returns:
    password digest
  */
  private function generatePasswordDigest( $password, $timestamp, $nonce) {
    $nonceBin = base64_decode( $nonce);
    $rawDigest = $nonceBin.$timestamp.$password;
    $sha1 = sha1( $rawDigest, true);
    
    return base64_encode( $sha1);
  }

  /*
    Generates a unique nonce value.

    Parameter:
    $prefix:  prefix used in nonce generation to create a unique nonce

    Returns:
    nonce value
  */
  private function generateNonce( $prefix) {
    return base64_encode( substr( md5( uniqid( $prefix.'_', true)), 0, 20));
  }

  /*
    Returns the current time as UTC timestamp.

    Returns:
    UTC timestamp
  */
  private function getUTCTimestamp() {
    $dateTime = new DateTime( "now", new DateTimeZone( 'UTC'));
    $str = $dateTime->format( 'Y-m-d H:i:s');
    
    $i = strpos( $str, ' ');
    $date = substr( $str, 0, $i);
    $time = substr( $str, $i + 1);
    
    return $date."T".$time."Z";
  }
}

?>
