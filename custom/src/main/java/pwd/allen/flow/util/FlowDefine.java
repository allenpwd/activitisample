package pwd.allen.flow.util;

import java.util.List;

/**
 * @author 流程定义实体类
 * @create 2020-10-09 11:20
 **/
public class FlowDefine {

    public static final String NODE_START = "开始";   //开始节点名称
    public static final String NODE_END = "结束";   //结束节点名称

    private String key;

    private List<Sequence> sequences;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<Sequence> getSequences() {
        return sequences;
    }

    public void setSequences(List<Sequence> sequences) {
        this.sequences = sequences;
    }

    public class Sequence {
        /**
         * 开始节点名
         */
        private String sourceNode;
        /**
         * 结束节点名
         */
        private String targetNode;
        /**
         * 节点跳转时执行的脚本代码
         */
        private String expression;

        public String getSourceNode() {
            return sourceNode;
        }

        public void setSourceNode(String sourceNode) {
            this.sourceNode = sourceNode;
        }

        public String getTargetNode() {
            return targetNode;
        }

        public void setTargetNode(String targetNode) {
            this.targetNode = targetNode;
        }

        public String getExpression() {
            return expression;
        }

        public void setExpression(String expression) {
            this.expression = expression;
        }
    }
}
