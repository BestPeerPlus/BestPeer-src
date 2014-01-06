package sg.edu.nus.gui.dbview;

import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * 
 * Implement node in a DBTree
 *
 */
public class DBTreeNode extends DefaultMutableTreeNode
{
	public static int SHARESTATUS_SHARE = 0;
	public static int SHARESTATUS_UNSHARE = 1;
	public static int SHARESTATUS_TEMPSHARE = 2;
	public static int SHARESTATUS_TEMPUNSHARE = 3;	
	
	//private members
	private static final long serialVersionUID = 5884128319679687283L;

	// the name of the column
	private String sourceSchemaName = null;
	private Vector<String> targetSchemaName = null;
	private String nodeType;
	
	private int shareStatus;
	
	private boolean selected = false;
	
	private boolean hasInsertIndex = false;
	
	public DBTreeNode(String name, boolean selected) {		
		super(name);
		
		if(name.indexOf("->") != -1)
		{
			String[] arrName = name.split("->");
			this.sourceSchemaName = arrName[0];
		}
		else
			this.sourceSchemaName = name;
		
		shareStatus = DBTreeNode.SHARESTATUS_UNSHARE;
		
		this.selected = selected;
		
		targetSchemaName = new Vector<String>();
	}
	
	public DBTreeNode()
	{
		
	}
	
	/**
	 * Constructor
	 * 
	 * @param columnName -  the name of the column
	 */
	public DBTreeNode(String name)
	{
		super(name);
		
		if(name.indexOf("->") != -1)
		{
			String[] arrName = name.split("->");
			this.sourceSchemaName = arrName[0];
		}
		else
			this.sourceSchemaName = name;
		
		shareStatus = DBTreeNode.SHARESTATUS_UNSHARE;
		
		targetSchemaName = new Vector<String>();
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
	 * @return the isSelected
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @param isSelected the isSelected to set
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * @return the targetSchemaName
	 */
	public Vector<String> getTargetSchemaName() {	
		return targetSchemaName;
	}

	/**
	 * @param targetSchemaName the targetSchemaName to set
	 */
	public void setTargetSchemaName(Vector<String> targetSchemaName) {
		this.targetSchemaName = targetSchemaName;
	}

	/**
	 * @return the hasInsertIndex
	 */
	public boolean isHasInsertIndex() {
		return hasInsertIndex;
	}

	/**
	 * @param hasInsertIndex the hasInsertIndex to set
	 */
	public void setHasInsertIndex(boolean hasInsertIndex) {
		this.hasInsertIndex = hasInsertIndex;
	}	
}
