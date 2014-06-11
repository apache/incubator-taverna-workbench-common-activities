package net.sf.taverna.t2.wadl.ui.serviceprovider;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.swing.Icon;
import javax.xml.bind.JAXBException;

import net.sf.taverna.t2.activities.rest.RESTActivity.HTTP_METHOD;
import net.sf.taverna.t2.activities.rest.RESTActivityConfigurationBean;
import net.sf.taverna.t2.activities.rest.ui.servicedescription.RESTActivityIcon;
import net.sf.taverna.t2.servicedescriptions.AbstractConfigurableServiceProvider;
import net.sf.taverna.t2.servicedescriptions.CustomizedConfigurePanelProvider;

import org.apache.log4j.Logger;
import org.jvnet.ws.wadl.Application;
import org.jvnet.ws.wadl.Param;
import org.jvnet.ws.wadl.ast.ApplicationNode;
import org.jvnet.ws.wadl.ast.InvalidWADLException;
import org.jvnet.ws.wadl.ast.MethodNode;
import org.jvnet.ws.wadl.ast.PathSegment;
import org.jvnet.ws.wadl.ast.RepresentationNode;
import org.jvnet.ws.wadl.ast.ResourceNode;
import org.jvnet.ws.wadl.ast.WadlAstBuilder;
import org.jvnet.ws.wadl.util.MessageListener;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

public class WadlServiceProvider extends
	AbstractConfigurableServiceProvider<WadlServiceProviderConfig> implements
	CustomizedConfigurePanelProvider<WadlServiceProviderConfig> {
	
	private static Logger logger = Logger.getLogger(WadlServiceProvider.class);
	
	public WadlServiceProvider() {
		super(new WadlServiceProviderConfig("http://example.com"));
	}

	private static final URI providerId = URI
		.create("http://example.com/2011/service-provider/wadl-activity");
	
	/**
	 * Do the actual search for services. Return using the callBack parameter.
	 */
	public void findServiceDescriptionsAsync(
			FindServiceDescriptionsCallBack callBack) {
		// Use callback.status() for long-running searches
		// callBack.status("Resolving example services");
		
//		final SchemaCompiler s2j = XJC.createSchemaCompiler();
        
        final Set<URI> jsonSchemas = new LinkedHashSet<URI>();
		
       WadlAstBuilder astBuilder = new WadlAstBuilder(
                new WadlAstBuilder.SchemaCallback() {

                    public void processSchema(InputSource input) {
                        
/*                        // Assume that the stream is a buffered stream at this point
                        // and mark a position
                        InputStream is = input.getByteStream();
                        is.mark(8192);
                        
                        // Read the first bytes and look for the xml header
                        //
                        String peakContent = null;
                        
                        try {
                            Reader r = new InputStreamReader(is, "UTF-8");
                            
                            CharBuffer cb = CharBuffer.allocate(20);
                            r.read(cb);
                            cb.flip();
                            peakContent = cb.toString();
                        }
                        catch (IOException e) {
                            throw new RuntimeException("Internal problem pushing back buffer", e);
                        } finally {
                            try {
                                is.reset();
                            } catch (IOException ex) {
                                throw new RuntimeException("Internal problem pushing back buffer", ex);
                            }
                            
                        }
                            
                        // By default assume a xml schema, better guess
                        // because some XML files don't start with <?xml
                        // as per bug WADL-66
                        if (peakContent.matches("^\\s*\\{")) {
                            // We are guessing this is a json type
                            jsonSchemas.add(URI.create(input.getSystemId()));
                        }
                        else { //if (peakContent==null || peakContent.contains("<?xml") || peakContent.startsWith("<")) {
                            s2j.parseSchema(input);
                        } 
 */                   
                    }

                    public void processSchema(String uri, Element node) {
/*                        s2j.parseSchema(uri, node);*/
                    }
                },
                new MessageListener() {

					@Override
					public void warning(String message, Throwable throwable) {
						logger.warn(message, throwable);
					}

					@Override
					public void info(String message) {
						logger.info(message);
					}

					@Override
					public void error(String message, Throwable throwable) {
						logger.error(message, throwable);
					}}) {
                    @Override 
                    protected Application processDescription(URI desc) throws JAXBException, IOException {
                        URLConnection conn = desc.toURL().openConnection();
                        conn.setRequestProperty("Accept", "application/vnd.sun.wadl+xml");
                        InputStream is = conn.getInputStream();
                        return processDescription(desc, is);
                    }
                };
       
       
		List<WadlServiceDesc> results = new ArrayList<WadlServiceDesc>();      

		try {
			ApplicationNode an = astBuilder.buildAst(new URI(this.getConfiguration().getUri()));
	        List<ResourceNode> rs = an.getResources();
            for (ResourceNode r: rs) {
                processEndpointClass(results, r);
            }

		} catch (InvalidWADLException | IOException | URISyntaxException e) {
			logger.error(e);
		}


				
	// partialResults() can also be called several times from inside
		// for-loop if the full search takes a long time
		callBack.partialResults(results);

		// No more results will be coming
		callBack.finished();
	}

	private void processEndpointClass(List<WadlServiceDesc> results, ResourceNode root) {
        for (ResourceNode r: root.getChildResources()) {
            processSubClass(results, r);
        }
	}

	private void processSubClass(List<WadlServiceDesc> results, ResourceNode resource) {
        // generate Java methods for each resource method
        for (MethodNode m: resource.getMethods()) {
            processMethodDecls(results, m);
        } 


        // generate sub classes for each child resource
        for (ResourceNode r: resource.getChildResources()) {
            processSubClass(results, r);
        }
	}

	private void processMethodDecls(List<WadlServiceDesc> results, MethodNode method) {
		RESTActivityConfigurationBean serviceConfigBean =
				RESTActivityConfigurationBean.getDefaultInstance();
		
		HTTP_METHOD httpMethod = HTTP_METHOD.GET;
		String methodName = method.getName();
		switch (methodName) {
		case "GET":
			httpMethod = HTTP_METHOD.GET;
			break;
		case "POST":
			httpMethod = HTTP_METHOD.POST;
			break;
		case "PUT":
			httpMethod = HTTP_METHOD.PUT;
			break;
		case "DELETE":
			httpMethod = HTTP_METHOD.DELETE;
			break;
		default:
			// This means that valid HTTP methods such as OPTIONS are ignored
			return;
		}

		
		serviceConfigBean.setHttpMethod(httpMethod);
		
    	StringBuilder sb = new StringBuilder();

		List<PathSegment> segments = method.getOwningResource()
				.getPathSegments();
		List<String> pathSegments = new ArrayList<String>();
		for (int segement = 0; segement < segments.size(); segement++) {
			String pathSegment = segments.get(segement).getTemplate();
			if (pathSegment.contains("{") && pathSegment.contains(":")) {
				String firstPart = pathSegment.substring(0, pathSegment.indexOf(":"));
				String secondPart = pathSegment.substring(pathSegment.indexOf("}"));
				pathSegment = firstPart + secondPart;
			}
			boolean bufferEndsInSlash = sb.length() > 0 ? sb
					.charAt(sb.length() - 1) == '/' : false;
			boolean pathSegmentStartsWithSlash = pathSegment.startsWith("/");

			if (pathSegmentStartsWithSlash && bufferEndsInSlash) {
				// We only need the one slash, so remove one from the
				// pathSegement
				sb.append(pathSegment, 1, pathSegment.length());
			} else if (pathSegmentStartsWithSlash || bufferEndsInSlash || (segement == 0)){
				// Only one has a slash so it is fine to append
				sb.append(pathSegment);
			} else {
				// Neither has one so add a slash in
				sb.append('/');
				sb.append(pathSegment);
			}
			if ("/".equals(pathSegment)) {
				pathSegments.add(pathSegment);
			} else {
				if (pathSegment.startsWith("/")) {
					pathSegments.add(pathSegment.substring(1));
				} else {
					pathSegments.add(pathSegment);
				}
			}
		}

    	if (!method.getQueryParameters().isEmpty()) {
    		boolean first = true;
    		for (Param p : method.getQueryParameters()) {
    			if (first) {
    				first = false;
    				sb.append("?");
    			} else {
    				sb.append("&");
    			}
    			String paramName = p.getName();
    			sb.append(paramName + "={" + paramName + "}");
    		}
    	}
    	
    	serviceConfigBean.setUrlSignature(sb.toString());
    	
        List<RepresentationNode> supportedInputs = method.getSupportedInputs();
        List<RepresentationNode> supportedOutputs = new ArrayList<RepresentationNode>();
        for (List<RepresentationNode> nodeList : method.getSupportedOutputs().values()) {
          for (RepresentationNode node : nodeList) {
            supportedOutputs.add(node);
          }
        }
        
        if (!supportedInputs.isEmpty()) {
        	serviceConfigBean.setContentTypeForUpdates(supportedInputs.get(0).getMediaType());
        }
        
        if (!supportedOutputs.isEmpty()) {
        	serviceConfigBean.setAcceptsHeaderValue(supportedOutputs.get(0).getMediaType());
        }
        
        // TODO Something about the headers
        
    	WadlServiceDesc newServiceDesc = new WadlServiceDesc(this.getConfiguration().getUri(),
    			pathSegments,
    			serviceConfigBean);
    	results.add(newServiceDesc);

 	}

	/**
	 * Icon for service provider
	 */
	public Icon getIcon() {
		return RESTActivityIcon.getRESTActivityIcon();
	}

	/**
	 * Name of service provider, appears in right click for 'Remove service
	 * provider'
	 */
	public String getName() {
		return "WADL service";
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	public String getId() {
		return providerId.toASCIIString();
	}

	@Override
	protected List<? extends Object> getIdentifyingData() {
		return Arrays.asList(getConfiguration().getUri());
	}

	@Override
	public void createCustomizedConfigurePanel(
			final CustomizedConfigureCallBack<WadlServiceProviderConfig> callBack) {
		AddWadlServiceDialog addWadlServiceDialog = new AddWadlServiceDialog() {
			@Override
			protected void addRegistry(String wadlURL) {

				WadlServiceProviderConfig providerConfig = new WadlServiceProviderConfig(wadlURL);					
				callBack.newProviderConfiguration(providerConfig);
			}
		};
		addWadlServiceDialog.setVisible(true);	
	}

}
