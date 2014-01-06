package sg.edu.nus.gui.dbview;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * 
 * Implement node in the DBTree for global schema
 *
 */
public class GlobalDBTreeNode extends DefaultMutableTreeNode
{
	public static int SHARESTATUS_SHARE = 0;
	public static int SHARESTATUS_UNSHARE = 1;
	public static int SHARESTATUS_TEMPSHARE = 2;
	public static int SHARESTATUS_TEMPUNSHARE = 3;	
	
	//private members
	private static final long serialVersionUID = 5884128319679687283L;

	// the name of the column
	private String sourceSchemaName = null;
	private String targetSchemaName = null;
	private String nodeType;
	
	private int shareStatus;
	
	/**
	 * Constructor
	 * 
	 * @param columnName -  the name of the column
	 */
	public GlobalDBTreeNode(String name)
	{
		super(name);
		
		this.targetSchemaName = name;
		
		shareStatus = DBTreeNode.SHARESTATUS_UNSHARE;
	}

	/**
	 * @return the nodeType
	 */
	public String getNodeType() {
		return nodeType;
	}

	/**
	 * @param nodeType the nodeType to set
	 */
	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

	/**
	 * @return the shareStatus
	 */
	public int getShareStatus() {
		return shareStatus;
	}

	/**
	 * @param shareStatus the shareStatus to set
	 */
	public void setShareStatus(int shareStatus) {
		this.shareStatus = shareStatus;
	}

	/**
	 * @return the sourceSchemaName
	 */
	public String getSourceSchemaName() {
		return sourceSchemaName;
	}

	/**
	 * @param sourceSchemaName the sourceSchemaName to set
	 */
	public void setSourceSchemaName(String sourceSchemaName) {
		this.sourceSchemaName = sourceSchemaName;
	}

	/**
	 * @return the targetSchemaName
	 */
	public String getTargetSchemaName() {
		
		
		
		return targetSchemaName;
	}

	/**
	 * @param targetSchemaName the targetSchemaName to set
	 */
	public void setTargetSchemaName(String targetSchemaName) {
		this.targetSchemaName = targetSchemaName;
	}	
}
