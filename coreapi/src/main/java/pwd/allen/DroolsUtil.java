package pwd.allen;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.drools.core.base.DefaultKnowledgeHelperFactory;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderErrors;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;

public class DroolsUtil {
	
	private KnowledgeBuilder knowledgeBuilder;
	private InternalKnowledgeBase kBase;
	private KieSession kSession;
	
    public void init() throws Exception {
    	if (kSession != null) {
    		destroy();
    	}
    	knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
    }
    
    /**
     * 载入规则类
     * @param rules
     * @throws Exception
     */
    public void addRules(Object... rules) throws Exception {
    	for (Object rule : rules) {
    		String name = null;
    		if (rule instanceof String) {
    			name = rule.toString();
    			knowledgeBuilder.add(ResourceFactory.newClassPathResource(rule.toString()), ResourceType.DRL);
    		}
			KnowledgeBuilderErrors errors = knowledgeBuilder.getErrors();
			String err = "载入规则【" + name + "】出错:";
			boolean ifErr = false;
			for (KnowledgeBuilderError error : errors) {
				err += error.getMessage() + "\n";
				ifErr = true;
			}
			if (ifErr) {
				throw new RuntimeException(err);
			}
		}
    }
    
    /**
     * 载入规则类
     * @param rules
     * @throws Exception
     */
    public void addRules(List<?> rules) throws Exception {
    	for (Object rule : rules) {
    		String name = null;
    		if (rule instanceof String) {
    			name = rule.toString();
    			knowledgeBuilder.add(ResourceFactory.newClassPathResource(rule.toString()), ResourceType.DRL);
    		}
			KnowledgeBuilderErrors errors = knowledgeBuilder.getErrors();
			String err = "载入规则【" + name + "】出错:";
			boolean ifErr = false;
			for (KnowledgeBuilderError error : errors) {
				err += error.getMessage() + "\n";
				ifErr = true;
			}
			if (ifErr) {
				throw new RuntimeException(err);
			}
		}
    }

    public KieSession getKSession() {
    	if (kSession == null) {
			kBase = KnowledgeBaseFactory.newKnowledgeBase();
			kBase.addPackages(knowledgeBuilder.getKnowledgePackages());
    		kSession = kBase.newKieSession();
    	}
		return kSession;
    }
	
    public KieSession getNewKSession() {
    	if (kSession != null) {
    		closeSession();
    	}
    	if (kBase == null) {
    		kBase = KnowledgeBaseFactory.newKnowledgeBase();
    		kBase.addPackages(knowledgeBuilder.getKnowledgePackages());
    	}
    	kSession = kBase.newKieSession();
		return kSession;
    }
    
    public void closeSession() {
    	if (kSession != null) {
    		kSession.dispose();
    	}
    	kSession = null;
    }
    
    public void destroy() {
    	closeSession();
    	kBase = null;
    	knowledgeBuilder = null;
    }

	public KnowledgeBuilder getKnowledgeBuilder() {
		return knowledgeBuilder;
	}

	public InternalKnowledgeBase getkBase() {
		return kBase;
	}

	public static void setGlobal(StatefulKnowledgeSession kSession, Map<String, Object> mapGlobal) {
		if (kSession != null && mapGlobal != null && mapGlobal.size() > 0) {
			for (Entry<String, Object> entry : mapGlobal.entrySet()) {
				try {
					kSession.setGlobal(entry.getKey(), entry.getValue());
				} catch (Exception e) {
				}
			}
		}
	}
}
