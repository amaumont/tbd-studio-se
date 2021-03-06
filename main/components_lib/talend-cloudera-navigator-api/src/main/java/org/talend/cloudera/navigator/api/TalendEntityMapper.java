package org.talend.cloudera.navigator.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.talend.cloudera.navigator.api.entity.TalendEntity;
import org.talend.cloudera.navigator.api.entity.TalendEntityChild;
import org.talend.cloudera.navigator.api.entity.TalendInputEntity;
import org.talend.cloudera.navigator.api.entity.TalendInputOutputEntity;
import org.talend.cloudera.navigator.api.entity.TalendOutputEntity;
import org.talend.cloudera.navigator.api.util.ClouderaAPIUtil;
import org.talend.cloudera.navigator.api.util.GeneratorID;

import com.cloudera.nav.sdk.model.entities.Entity;

/**
 * Class to map studio components into cloudera navigator connected entities
 */
public class TalendEntityMapper {

    private static final String ENTITY_DESCRIPTION = "Talend Component";

    private static final String ENTITY_LINK = "http://www.talend.com/";

    private List<NavigatorNode> navigatorNodes;

    // The mapper uses a jobId to create unique entities in Cloudera navigator
    private String jobId;

    private StringBuilder debugStringBuilder;

    private List<String> tags = new ArrayList<String>();

    public TalendEntityMapper(List<NavigatorNode> navigatorNodes, String jobId) {
        this.navigatorNodes = navigatorNodes;
        this.jobId = jobId;
        this.debugStringBuilder = new StringBuilder();
        this.tags.add(GeneratorID.CLOUDERA_NAVIGATOR_APPLICATION_NAMESPACE);
    }

    /**
     * returns a list of Cloudera navigator entities ready to be written to Cloudera Navigator Calls methods to create
     * all of the entities and to connect them together
     */
    public List<Entity> map() {

        List<Entity> output = new ArrayList<Entity>();

        if (this.navigatorNodes.size() > 0) {
            for (NavigatorNode navigatorNode : this.navigatorNodes) {
                TalendEntity parentEntity = mapToParentEntity(navigatorNode);
                List<TalendEntityChild> childrenEntities = mapToChildrenEntities(navigatorNode.getSchema(),
                        parentEntity.getName());
                connectChildrenToParent(parentEntity, childrenEntities);
                connectChildrenToTarget(navigatorNode, childrenEntities);
                output.addAll(childrenEntities);
                parentEntity.connectToEntity(navigatorNode.getInputNodes(), navigatorNode.getOutputNodes());
                output.add(parentEntity);
                addToDebugString(parentEntity, childrenEntities);
            }
            return output;
        } else {
            throw new IllegalArgumentException("Empty Navigator Nodes list");
        }
    }

    /**
     * Map Navigator nodes to the equivalent Talend entities. Processing components are mapped to
     * TalendInputOutputEntity Input/output file components are mapped to TalendInputOutputEntity with a dataSet as
     * input/output Input/output components (LogRow, FixedFlow, ...) are mapped to TalendInputEntity/TalendOutputEntity
     *
     * ID = NameSpace + TALEND_JOB_ID + ComponentName
     */
    public TalendEntity mapToParentEntity(NavigatorNode navigatorNode) {
        List<String> inputNodes = navigatorNode.getInputNodes();
        List<String> outputNodes = navigatorNode.getOutputNodes();
        String componentName = navigatorNode.getName();

        TalendEntity talendEntity;
        if (((CollectionUtils.isNotEmpty(inputNodes)) && (CollectionUtils.isNotEmpty(outputNodes)))
                || ((CollectionUtils.isEmpty(inputNodes)) && (ClouderaAPIUtil.isFileInputOutputComponent(componentName)))
                || ((CollectionUtils.isEmpty(outputNodes)) && (ClouderaAPIUtil.isFileInputOutputComponent(componentName)))) {
            talendEntity = new TalendInputOutputEntity(getJobId(), navigatorNode.getName());
        } else if ((CollectionUtils.isEmpty(inputNodes)) && (CollectionUtils.isNotEmpty(outputNodes))) {
            talendEntity = new TalendInputEntity(getJobId(), navigatorNode.getName());
        } else if ((CollectionUtils.isNotEmpty(inputNodes)) && (CollectionUtils.isEmpty(outputNodes))) {
            talendEntity = new TalendOutputEntity(getJobId(), navigatorNode.getName());
        } else {
            throw new IllegalArgumentException("Unconnected Navigator Node : " + navigatorNode);
        }
        setEntityMetadata(talendEntity);
        return talendEntity;
    }

    /**
     * Add metadata/tags to cloudera navigator parent entities
     */
    public void setEntityMetadata(TalendEntity talendEntity) {
        talendEntity.setDescription(ENTITY_DESCRIPTION);
        talendEntity.setLink(ENTITY_LINK);
        talendEntity.setTags(this.tags);
    }

    /**
     * Add generic tag to all the talend entities
     *
     * @param tag the tag to add
     */
    public void addTag(String tag) {
        this.tags.add(tag);
    }

    /**
     * Map the schema of a navigator node to a TalendEntityChild. The TalendEntityChild will represent the output schema
     * of a Talend component.
     *
     * ID = NameSpace + TALEND_JOB_ID + ParentName + ColumnName + ColumnsType
     */
    public List<TalendEntityChild> mapToChildrenEntities(Map<String, String> schema, String ParentEntityName) {

        List<TalendEntityChild> output = new ArrayList<TalendEntityChild>();
        for (Entry<String, String> entry : schema.entrySet()) {
            TalendEntityChild talendEntityChild = new TalendEntityChild(getJobId(), ParentEntityName, entry.getKey(),
                    entry.getValue());
            setChildEntityMetadata(talendEntityChild);
            output.add(talendEntityChild);
        }
        return output;
    }

    /**
     * Add metadata/tags to cloudera navigator children entities
     */
    public void setChildEntityMetadata(TalendEntityChild talendEntityChild) {
        talendEntityChild.setDescription(ENTITY_DESCRIPTION);
        talendEntityChild.setLink(ENTITY_LINK);
    }

    /**
     * Connect schema entities to their parent entity using CHILD -> PARENT relation
     */
    public void connectChildrenToParent(TalendEntity parentEntity, List<TalendEntityChild> children) {
        for (TalendEntityChild child : children) {
            child.setParent(parentEntity);
        }
    }

    /**
     * Connect children to their target entity
     *
     * Note : Due to limitations in the Cloudera navigator API/SDK we need to connect each {@link #TalendEntityChild} to
     * the next {@link #TalendEntity} in the flow in order to make visible in the navigator
     */
    public void connectChildrenToTarget(NavigatorNode navigatorNode, List<TalendEntityChild> children) {
        for (TalendEntityChild talendEntityChild : children) {
            if (CollectionUtils.isEmpty(navigatorNode.getOutputNodes())) {
                // Output components
                if (ClouderaAPIUtil.isFileInputOutputComponent(navigatorNode.getName())) {
                    // File Output children entities should be linked with a dataset
                    String targetComponentId = GeneratorID.generateDatasetID(getJobId(), navigatorNode.getName());
                    talendEntityChild.addTarget(targetComponentId);
                } else {
                    // For Output terminal components (tLogRow, ...)
                    // We connect the children to the component itself
                    // TODO: as soon as the API allow us to do that, remove this link.
                    String targetComponentId = GeneratorID.generateNodeID(getJobId(), navigatorNode.getName());
                    talendEntityChild.addTarget(targetComponentId);
                }
            } else {
                Boolean childConnected = false;
                for (String outputComponent : navigatorNode.getOutputNodes()) {
                    if (ClouderaAPIUtil.isThisComponentContainsThisField(outputComponent, talendEntityChild.getName(),
                            this.navigatorNodes)) {
                        String targetComponentId = GeneratorID.generateEntityChildID(getJobId(), outputComponent,
                                talendEntityChild.getName());
                        talendEntityChild.addTarget(targetComponentId);
                        childConnected = true;
                    }
                }
                // TODO: as soon as the API allow us to do that, remove this link.
                if (!childConnected) {
                    // This child is never used after this component, we link it to its parent.
                    String targetComponentId = GeneratorID.generateNodeID(getJobId(), navigatorNode.getName());
                    talendEntityChild.addTarget(targetComponentId);
                }
            }
        }
    }

    /**
     * Add to output debug string.
     */
    public void addToDebugString(TalendEntity parentEntity, List<TalendEntityChild> children) {
        this.debugStringBuilder.append("\n" + parentEntity.toString() + "\n");
        for (TalendEntityChild child : children) {
            this.debugStringBuilder.append("\t" + child.toString() + "\n");
        }
    }

    @Override
    public String toString() {
        return this.debugStringBuilder.toString();
    }

    public String getJobId() {
        return jobId;
    }
}
