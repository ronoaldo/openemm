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

// Import the WSSE SOAP client for use of EMM/OpenEMM Webservice API 2.0
include("WSSESoapClient.php");

// URL of the WSDL document. Modify that for your environment
$wsdlURL = "http://localhost:8080/openemm-ws/emmservices.wsdl";

// Your authentication information
$username = "set your username here";
$password = "set your password here";

// Create new SOAP client
$client = new WsseSoapClient( $wsdlURL, $username, $password);

// Example: Retrieve list of all available webservices
var_dump( $client->__getFunctions());

// Example: Retrieve list of all mailings
var_dump( $client->ListMailings());
?>
