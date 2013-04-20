/**
 * RemoteCMTemplateManagerSoapBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.agnitas.cms.webservices.generated;

public class RemoteCMTemplateManagerSoapBindingStub extends org.apache.axis.client.Stub
		implements
		org.agnitas.cms.webservices.generated.RemoteCMTemplateManager_PortType {
	private java.util.Vector cachedSerClasses = new java.util.Vector();
	private java.util.Vector cachedSerQNames = new java.util.Vector();
	private java.util.Vector cachedSerFactories = new java.util.Vector();
	private java.util.Vector cachedDeserFactories = new java.util.Vector();

	static org.apache.axis.description.OperationDesc[] _operations;

	static {
		_operations = new org.apache.axis.description.OperationDesc[16];
		_initOperationDesc1();
		_initOperationDesc2();
	}

	private static void _initOperationDesc1() {
		org.apache.axis.description.OperationDesc oper;
		org.apache.axis.description.ParameterDesc param;
		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("createCMTemplate");
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("", "template"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName(
						"http://beans.mock.dataaccess.utils.cms.agnitas.org",
						"CMTemplate"),
				org.agnitas.cms.webservices.generated.CMTemplate.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName(
				"http://beans.mock.dataaccess.utils.cms.agnitas.org", "CMTemplate"));
		oper.setReturnClass(org.agnitas.cms.webservices.generated.CMTemplate.class);
		oper.setReturnQName(new javax.xml.namespace.QName("", "createCMTemplateReturn"));
		oper.setStyle(org.apache.axis.constants.Style.RPC);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[0] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("getCMTemplate");
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("", "id"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"),
				int.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName(
				"http://beans.mock.dataaccess.utils.cms.agnitas.org", "CMTemplate"));
		oper.setReturnClass(org.agnitas.cms.webservices.generated.CMTemplate.class);
		oper.setReturnQName(new javax.xml.namespace.QName("", "getCMTemplateReturn"));
		oper.setStyle(org.apache.axis.constants.Style.RPC);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[1] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("getCMTemplates");
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("", "companyId"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"),
				int.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName(
				"http://mock.dataaccess.utils.cms.agnitas.org", "ArrayOf_xsd_anyType"));
		oper.setReturnClass(java.lang.Object[].class);
		oper.setReturnQName(new javax.xml.namespace.QName("", "getCMTemplatesReturn"));
		param = oper.getReturnParamDesc();
		param.setItemQName(new javax.xml.namespace.QName("", "item"));
		oper.setStyle(org.apache.axis.constants.Style.RPC);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[2] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("getCMTemplatesSortByName");
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("", "companyId"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"),
				int.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("", "sortDirection"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema",
						"string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName(
				"http://mock.dataaccess.utils.cms.agnitas.org", "ArrayOf_xsd_anyType"));
		oper.setReturnClass(java.lang.Object[].class);
		oper.setReturnQName(
				new javax.xml.namespace.QName("", "getCMTemplatesSortByNameReturn"));
		param = oper.getReturnParamDesc();
		param.setItemQName(new javax.xml.namespace.QName("", "item"));
		oper.setStyle(org.apache.axis.constants.Style.RPC);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[3] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("deleteCMTemplate");
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("", "id"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"),
				int.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
		oper.setStyle(org.apache.axis.constants.Style.RPC);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[4] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("updateCMTemplate");
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("", "id"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"),
				int.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("", "name"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema",
						"string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("", "description"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema",
						"string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema",
						"boolean"));
		oper.setReturnClass(boolean.class);
		oper.setReturnQName(new javax.xml.namespace.QName("", "updateCMTemplateReturn"));
		oper.setStyle(org.apache.axis.constants.Style.RPC);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[5] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("updateContent");
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("", "id"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"),
				int.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("", "content"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema",
						"base64Binary"), byte[].class, false, false);
		oper.addParameter(param);
		oper.setReturnType(
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema",
						"boolean"));
		oper.setReturnClass(boolean.class);
		oper.setReturnQName(new javax.xml.namespace.QName("", "updateContentReturn"));
		oper.setStyle(org.apache.axis.constants.Style.RPC);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[6] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("addMailingBindings");
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("", "cmTemplateId"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"),
				int.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("", "mailingIds"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName(
						"http://mock.dataaccess.utils.cms.agnitas.org",
						"ArrayOf_xsd_anyType"), java.lang.Object[].class, false, false);
		param.setItemQName(new javax.xml.namespace.QName("", "item"));
		oper.addParameter(param);
		oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
		oper.setStyle(org.apache.axis.constants.Style.RPC);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[7] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("getCMTemplateForMailing");
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("", "mailingId"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"),
				int.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName(
				"http://beans.mock.dataaccess.utils.cms.agnitas.org", "CMTemplate"));
		oper.setReturnClass(org.agnitas.cms.webservices.generated.CMTemplate.class);
		oper.setReturnQName(
				new javax.xml.namespace.QName("", "getCMTemplateForMailingReturn"));
		oper.setStyle(org.apache.axis.constants.Style.RPC);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[8] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("removeMailingBindings");
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("", "mailingIds"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName(
						"http://mock.dataaccess.utils.cms.agnitas.org",
						"ArrayOf_xsd_anyType"), java.lang.Object[].class, false, false);
		param.setItemQName(new javax.xml.namespace.QName("", "item"));
		oper.addParameter(param);
		oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
		oper.setStyle(org.apache.axis.constants.Style.RPC);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[9] = oper;

	}

	private static void _initOperationDesc2() {
		org.apache.axis.description.OperationDesc oper;
		org.apache.axis.description.ParameterDesc param;
		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("getMailingBindingWrapper");
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("", "cmTemplate"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"),
				int.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName(
				"http://mock.dataaccess.utils.cms.agnitas.org", "ArrayOf_xsd_anyType"));
		oper.setReturnClass(java.lang.Object[].class);
		oper.setReturnQName(
				new javax.xml.namespace.QName("", "getMailingBindingWrapperReturn"));
		param = oper.getReturnParamDesc();
		param.setItemQName(new javax.xml.namespace.QName("", "item"));
		oper.setStyle(org.apache.axis.constants.Style.RPC);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[10] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("getMailingBindingArrayWrapper");
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("", "mailingIds"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName(
						"http://mock.dataaccess.utils.cms.agnitas.org",
						"ArrayOf_xsd_anyType"), java.lang.Object[].class, false, false);
		param.setItemQName(new javax.xml.namespace.QName("", "item"));
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName(
				"http://mock.dataaccess.utils.cms.agnitas.org", "ArrayOf_xsd_anyType"));
		oper.setReturnClass(java.lang.Object[].class);
		oper.setReturnQName(
				new javax.xml.namespace.QName("", "getMailingBindingArrayWrapperReturn"));
		param = oper.getReturnParamDesc();
		param.setItemQName(new javax.xml.namespace.QName("", "item"));
		oper.setStyle(org.apache.axis.constants.Style.RPC);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[11] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("getTextVersion");
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("", "adminId"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"),
				int.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema",
						"string"));
		oper.setReturnClass(java.lang.String.class);
		oper.setReturnQName(new javax.xml.namespace.QName("", "getTextVersionReturn"));
		oper.setStyle(org.apache.axis.constants.Style.RPC);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[12] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("removeTextVersion");
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("", "adminId"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"),
				int.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
		oper.setStyle(org.apache.axis.constants.Style.RPC);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[13] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("saveTextVersion");
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("", "adminId"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"),
				int.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("", "text"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema",
						"string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
		oper.setStyle(org.apache.axis.constants.Style.RPC);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[14] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("getMailingWithCmsContent");
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("", "mailingIds"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName(
						"http://mock.dataaccess.utils.cms.agnitas.org",
						"ArrayOf_xsd_anyType"), java.lang.Object[].class, false, false);
		param.setItemQName(new javax.xml.namespace.QName("", "item"));
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("", "companyId"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"),
				int.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName(
				"http://mock.dataaccess.utils.cms.agnitas.org", "ArrayOf_xsd_anyType"));
		oper.setReturnClass(java.lang.Object[].class);
		oper.setReturnQName(
				new javax.xml.namespace.QName("", "getMailingWithCmsContentReturn"));
		param = oper.getReturnParamDesc();
		param.setItemQName(new javax.xml.namespace.QName("", "item"));
		oper.setStyle(org.apache.axis.constants.Style.RPC);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[15] = oper;

	}

	public RemoteCMTemplateManagerSoapBindingStub() throws org.apache.axis.AxisFault {
		this(null);
	}

	public RemoteCMTemplateManagerSoapBindingStub(java.net.URL endpointURL,
												  javax.xml.rpc.Service service) throws
			org.apache.axis.AxisFault {
		this(service);
		super.cachedEndpoint = endpointURL;
	}

	public RemoteCMTemplateManagerSoapBindingStub(javax.xml.rpc.Service service) throws
			org.apache.axis.AxisFault {
		if(service == null) {
			super.service = new org.apache.axis.client.Service();
		} else {
			super.service = service;
		}
		((org.apache.axis.client.Service) super.service).setTypeMappingVersion("1.2");
		java.lang.Class cls;
		javax.xml.namespace.QName qName;
		javax.xml.namespace.QName qName2;
		java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
		java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
		java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
		java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
		java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
		java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
		java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
		java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
		java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
		java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
		qName = new javax.xml.namespace.QName(
				"http://beans.mock.dataaccess.utils.cms.agnitas.org", "CMTemplate");
		cachedSerQNames.add(qName);
		cls = org.agnitas.cms.webservices.generated.CMTemplate.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName(
				"http://mock.dataaccess.utils.cms.agnitas.org", "ArrayOf_xsd_anyType");
		cachedSerQNames.add(qName);
		cls = java.lang.Object[].class;
		cachedSerClasses.add(cls);
		qName = new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema",
				"anyType");
		qName2 = new javax.xml.namespace.QName("", "item");
		cachedSerFactories
				.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName,
						qName2));
		cachedDeserFactories
				.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

	}

	protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
		try {
			org.apache.axis.client.Call _call = super._createCall();
			if(super.maintainSessionSet) {
				_call.setMaintainSession(super.maintainSession);
			}
			if(super.cachedUsername != null) {
				_call.setUsername(super.cachedUsername);
			}
			if(super.cachedPassword != null) {
				_call.setPassword(super.cachedPassword);
			}
			if(super.cachedEndpoint != null) {
				_call.setTargetEndpointAddress(super.cachedEndpoint);
			}
			if(super.cachedTimeout != null) {
				_call.setTimeout(super.cachedTimeout);
			}
			if(super.cachedPortName != null) {
				_call.setPortName(super.cachedPortName);
			}
			java.util.Enumeration keys = super.cachedProperties.keys();
			while(keys.hasMoreElements()) {
				java.lang.String key = (java.lang.String) keys.nextElement();
				_call.setProperty(key, super.cachedProperties.get(key));
			}
			// All the type mapping information is registered
			// when the first call is made.
			// The type mapping information is actually registered in
			// the TypeMappingRegistry of the service, which
			// is the reason why registration is only needed for the first call.
			synchronized(this) {
				if(firstCall()) {
					// must set encoding style before registering serializers
					_call.setEncodingStyle(null);
					for(int i = 0; i < cachedSerFactories.size(); ++i) {
						java.lang.Class cls = (java.lang.Class) cachedSerClasses.get(i);
						javax.xml.namespace.QName qName =
								(javax.xml.namespace.QName) cachedSerQNames.get(i);
						java.lang.Object x = cachedSerFactories.get(i);
						if(x instanceof Class) {
							java.lang.Class sf = (java.lang.Class)
									cachedSerFactories.get(i);
							java.lang.Class df = (java.lang.Class)
									cachedDeserFactories.get(i);
							_call.registerTypeMapping(cls, qName, sf, df, false);
						} else if(x instanceof javax.xml.rpc.encoding.SerializerFactory) {
							org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory)
									cachedSerFactories.get(i);
							org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory)
									cachedDeserFactories.get(i);
							_call.registerTypeMapping(cls, qName, sf, df, false);
						}
					}
				}
			}
			return _call;
		}
		catch(java.lang.Throwable _t) {
			throw new org.apache.axis.AxisFault("Failure trying to get the Call object",
					_t);
		}
	}

	public org.agnitas.cms.webservices.generated.CMTemplate createCMTemplate(
			org.agnitas.cms.webservices.generated.CMTemplate template) throws
			java.rmi.RemoteException {
		if(super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[0]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName(
				"http://mock.dataaccess.utils.cms.agnitas.org", "createCMTemplate"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp = _call.invoke(new java.lang.Object[]{template});

			if(_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				try {
					return (org.agnitas.cms.webservices.generated.CMTemplate) _resp;
				} catch(java.lang.Exception _exception) {
					return (org.agnitas.cms.webservices.generated.CMTemplate) org.apache
							.axis.utils.JavaUtils.convert(_resp,
									org.agnitas.cms.webservices.generated.CMTemplate.class);
				}
			}
		} catch(org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public org.agnitas.cms.webservices.generated.CMTemplate getCMTemplate(int id) throws
			java.rmi.RemoteException {
		if(super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[1]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName(
				"http://mock.dataaccess.utils.cms.agnitas.org", "getCMTemplate"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp = _call
					.invoke(new java.lang.Object[]{new java.lang.Integer(id)});

			if(_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				try {
					return (org.agnitas.cms.webservices.generated.CMTemplate) _resp;
				} catch(java.lang.Exception _exception) {
					return (org.agnitas.cms.webservices.generated.CMTemplate) org.apache
							.axis.utils.JavaUtils.convert(_resp,
									org.agnitas.cms.webservices.generated.CMTemplate.class);
				}
			}
		} catch(org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public java.lang.Object[] getCMTemplates(int companyId) throws
			java.rmi.RemoteException {
		if(super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[2]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName(
				"http://mock.dataaccess.utils.cms.agnitas.org", "getCMTemplates"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp = _call
					.invoke(new java.lang.Object[]{new java.lang.Integer(companyId)});

			if(_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				try {
					return (java.lang.Object[]) _resp;
				} catch(java.lang.Exception _exception) {
					return (java.lang.Object[]) org.apache.axis.utils.JavaUtils
							.convert(_resp, java.lang.Object[].class);
				}
			}
		} catch(org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public java.lang.Object[] getCMTemplatesSortByName(int companyId,
													   java.lang.String sortDirection) throws
			java.rmi.RemoteException {
		if(super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[3]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName(
				"http://mock.dataaccess.utils.cms.agnitas.org",
				"getCMTemplatesSortByName"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp = _call
					.invoke(new java.lang.Object[]{new java.lang.Integer(companyId),
							sortDirection});

			if(_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				try {
					return (java.lang.Object[]) _resp;
				} catch(java.lang.Exception _exception) {
					return (java.lang.Object[]) org.apache.axis.utils.JavaUtils
							.convert(_resp, java.lang.Object[].class);
				}
			}
		} catch(org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public void deleteCMTemplate(int id) throws java.rmi.RemoteException {
		if(super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[4]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName(
				"http://mock.dataaccess.utils.cms.agnitas.org", "deleteCMTemplate"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp = _call
					.invoke(new java.lang.Object[]{new java.lang.Integer(id)});

			if(_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			}
			extractAttachments(_call);
		} catch(org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public boolean updateCMTemplate(int id, java.lang.String name,
									java.lang.String description) throws
			java.rmi.RemoteException {
		if(super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[5]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName(
				"http://mock.dataaccess.utils.cms.agnitas.org", "updateCMTemplate"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp = _call
					.invoke(new java.lang.Object[]{new java.lang.Integer(id), name,
							description});

			if(_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				try {
					return ((java.lang.Boolean) _resp).booleanValue();
				} catch(java.lang.Exception _exception) {
					return ((java.lang.Boolean) org.apache.axis.utils.JavaUtils
							.convert(_resp, boolean.class)).booleanValue();
				}
			}
		} catch(org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public boolean updateContent(int id, byte[] content) throws java.rmi.RemoteException {
		if(super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[6]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName(
				"http://mock.dataaccess.utils.cms.agnitas.org", "updateContent"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp = _call
					.invoke(new java.lang.Object[]{new java.lang.Integer(id), content});

			if(_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				try {
					return ((java.lang.Boolean) _resp).booleanValue();
				} catch(java.lang.Exception _exception) {
					return ((java.lang.Boolean) org.apache.axis.utils.JavaUtils
							.convert(_resp, boolean.class)).booleanValue();
				}
			}
		} catch(org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public void addMailingBindings(int cmTemplateId, java.lang.Object[] mailingIds) throws
			java.rmi.RemoteException {
		if(super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[7]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName(
				"http://mock.dataaccess.utils.cms.agnitas.org", "addMailingBindings"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp = _call
					.invoke(new java.lang.Object[]{new java.lang.Integer(cmTemplateId),
							mailingIds});

			if(_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			}
			extractAttachments(_call);
		} catch(org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public org.agnitas.cms.webservices.generated.CMTemplate getCMTemplateForMailing(
			int mailingId) throws java.rmi.RemoteException {
		if(super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[8]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName(
				"http://mock.dataaccess.utils.cms.agnitas.org",
				"getCMTemplateForMailing"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp = _call
					.invoke(new java.lang.Object[]{new java.lang.Integer(mailingId)});

			if(_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				try {
					return (org.agnitas.cms.webservices.generated.CMTemplate) _resp;
				} catch(java.lang.Exception _exception) {
					return (org.agnitas.cms.webservices.generated.CMTemplate) org.apache
							.axis.utils.JavaUtils.convert(_resp,
									org.agnitas.cms.webservices.generated.CMTemplate.class);
				}
			}
		} catch(org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public void removeMailingBindings(java.lang.Object[] mailingIds) throws
			java.rmi.RemoteException {
		if(super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[9]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName(
				"http://mock.dataaccess.utils.cms.agnitas.org", "removeMailingBindings"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp = _call.invoke(new java.lang.Object[]{mailingIds});

			if(_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			}
			extractAttachments(_call);
		} catch(org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public java.lang.Object[] getMailingBindingWrapper(int cmTemplate) throws
			java.rmi.RemoteException {
		if(super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[10]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName(
				"http://mock.dataaccess.utils.cms.agnitas.org",
				"getMailingBindingWrapper"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp = _call
					.invoke(new java.lang.Object[]{new java.lang.Integer(cmTemplate)});

			if(_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				try {
					return (java.lang.Object[]) _resp;
				} catch(java.lang.Exception _exception) {
					return (java.lang.Object[]) org.apache.axis.utils.JavaUtils
							.convert(_resp, java.lang.Object[].class);
				}
			}
		} catch(org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public java.lang.Object[] getMailingBindingArrayWrapper(
			java.lang.Object[] mailingIds) throws java.rmi.RemoteException {
		if(super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[11]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName(
				"http://mock.dataaccess.utils.cms.agnitas.org",
				"getMailingBindingArrayWrapper"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp = _call.invoke(new java.lang.Object[]{mailingIds});

			if(_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				try {
					return (java.lang.Object[]) _resp;
				} catch(java.lang.Exception _exception) {
					return (java.lang.Object[]) org.apache.axis.utils.JavaUtils
							.convert(_resp, java.lang.Object[].class);
				}
			}
		} catch(org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public java.lang.String getTextVersion(int adminId) throws java.rmi.RemoteException {
		if(super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[12]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName(
				"http://mock.dataaccess.utils.cms.agnitas.org", "getTextVersion"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp = _call
					.invoke(new java.lang.Object[]{new java.lang.Integer(adminId)});

			if(_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				try {
					return (java.lang.String) _resp;
				} catch(java.lang.Exception _exception) {
					return (java.lang.String) org.apache.axis.utils.JavaUtils
							.convert(_resp, java.lang.String.class);
				}
			}
		} catch(org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public void removeTextVersion(int adminId) throws java.rmi.RemoteException {
		if(super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[13]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName(
				"http://mock.dataaccess.utils.cms.agnitas.org", "removeTextVersion"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp = _call
					.invoke(new java.lang.Object[]{new java.lang.Integer(adminId)});

			if(_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			}
			extractAttachments(_call);
		} catch(org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public void saveTextVersion(int adminId, java.lang.String text) throws
			java.rmi.RemoteException {
		if(super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[14]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName(
				"http://mock.dataaccess.utils.cms.agnitas.org", "saveTextVersion"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp = _call
					.invoke(new java.lang.Object[]{new java.lang.Integer(adminId), text});

			if(_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			}
			extractAttachments(_call);
		} catch(org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public java.lang.Object[] getMailingWithCmsContent(java.lang.Object[] mailingIds,
													   int companyId) throws
			java.rmi.RemoteException {
		if(super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[15]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName(
				"http://mock.dataaccess.utils.cms.agnitas.org",
				"getMailingWithCmsContent"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp = _call.invoke(new java.lang.Object[]{mailingIds,
					new java.lang.Integer(companyId)});

			if(_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				try {
					return (java.lang.Object[]) _resp;
				} catch(java.lang.Exception _exception) {
					return (java.lang.Object[]) org.apache.axis.utils.JavaUtils
							.convert(_resp, java.lang.Object[].class);
				}
			}
		} catch(org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

}
