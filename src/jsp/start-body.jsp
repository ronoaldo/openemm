<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<% int i=0; %>

              <table border="0" cellspacing="0" cellpadding="0">
                <tr>  
                  <td>
                     <table border="0" cellspacing="10" cellpadding="0">
                        <tr>
                        <agn:ShowNavigation navigation="sidemenu" highlightKey="">
                           <agn:ShowByPermission token="<%= _navigation_token %>">
                              <% if(i==2) { %> </tr><tr> <% i=0; } i++; %>
                              <td>
                                  <table width="300" cellspacing="0" cellpadding="0">
                                  <tr>
                                      <td width="40"><html:link page="<%= new String(_navigation_href) %>"><img border="0" width="40" height="38" src="${emmLayoutBase.imagesURL}/splash_<%= _navigation_navMsg.toLowerCase() %>.gif" alt="<bean:message key='<%= _navigation_navMsg %>'/>"></html:link></td>
                                      <td class="boxhead" width="250"><html:link page="<%= _navigation_href %>"><span class="head1"><bean:message key="<%= _navigation_navMsg %>"/></span></html:link></td>
                                      <td width="10"><img border="0" width="10" height="38" src="${emmLayoutBase.imagesURL}/box_topright.gif" alt="top right"></td>
                                  </tr><tr>
                                      <td colspan=3 class="boxmiddle" height="80" width="300"><img src="images/emm/one_pixel.gif" alt="spacer" width=1 height=60 align="left"><html:link page="<%= _navigation_href %>"><bean:message key='<%= new String(\"splash.\"+_navigation_navMsg) %>'/></html:link></td>
                                  </tr><tr>
                                      <td width="40"><img border="0" width="40" height="10" src="${emmLayoutBase.imagesURL}/box_bottomleft.gif" alt="<bean:message key='<%= _navigation_navMsg %>'/>"></td>
                                      <td class="boxbottom"></td>
                                      <td width="10"><img border="0" width="10" height="10" src="${emmLayoutBase.imagesURL}/box_bottomright.gif" alt="bottom right"></td>
                                  </tr>
                              </table>
                              </td>
                              
                           </agn:ShowByPermission>
                        </agn:ShowNavigation>
                        </tr>
                     </table>
                  </td>
                </tr>
              </table>

