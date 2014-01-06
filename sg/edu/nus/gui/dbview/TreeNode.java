package sg.edu.nus.gui.dbview;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * 
 * Extending javax.swing.tree.DefaultMutableTreeNode with node type attribute
 *
 */

public class TreeNode extends DefaultMutableTreeNode
{
	private String type;
	
	public TreeNode(String name)
	{
		super(name);
	}
	
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	public String getName()
	{
		return super.toString();
	}
}
