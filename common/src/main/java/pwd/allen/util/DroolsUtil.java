package pwd.allen.util;

import org.drools.*;
import org.drools.builder.*;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class DroolsUtil {
	
	private KnowledgeBuilder knowledgeBuilder;
	private KnowledgeBase kBase;
	private StatefulKnowledgeSession kSession;
	
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
    			knowledgeBuilder.add(ResourceFactory.newClassPathResource(rule.toString()), org.drools.builder.ResourceType.DRL);
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

    public StatefulKnowledgeSession getKSession() {
    	if (kSession == null) {
			kBase = KnowledgeBaseFactory.newKnowledgeBase();
			kBase.addKnowledgePackages(knowledgeBuilder.getKnowledgePackages());
    		kSession = kBase.newStatefulKnowledgeSession();
    	}
		return kSession;
    }
	
    public StatefulKnowledgeSession getNewKSession() {
    	if (kSession != null) {
    		closeSession();
    	}
    	if (kBase == null) {
    		kBase = KnowledgeBaseFactory.newKnowledgeBase();
    		kBase.addKnowledgePackages(knowledgeBuilder.getKnowledgePackages());
    	}
    	kSession = kBase.newStatefulKnowledgeSession();
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

	public KnowledgeBase getkBase() {
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
