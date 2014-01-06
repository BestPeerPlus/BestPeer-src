package sg.edu.nus.gui;

import java.awt.BorderLayout;
import java.util.Vector;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.event.TreeSelectionListener;

/**
 * A tree panel with JTree, supporting adding and remove node
 * @author VHTam
 *
 */
public class DynamicTreePanel extends JPanel{

	private static final long serialVersionUID = 3247829861898073143L;
	
	protected DefaultMutableTreeNode rootNode;
    protected DefaultTreeModel treeModel;
    protected JTree tree;
    
    public DynamicTreePanel(String rootName) {
    	
        //super(new GridLayout(1,0));
    	super(new BorderLayout());
        
        rootNode = new DefaultMutableTreeNode(rootName);
        
        treeModel = new DefaultTreeModel(rootNode);

        tree = new JTree(treeModel);
        		
        tree.setEditable(true);
        tree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setShowsRootHandles(true);

        JScrollPane scrollPane = new JScrollPane(tree);
        
        //add(scrollPane);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    /** Remove all nodes except the root node. */
    public void clear() {
        rootNode.removeAllChildren();
        treeModel.reload();
    }

    public JTree getJTree(){
    	return tree;
    }
    
    public void addTreeListener(TreeSelectionListener treeListener){
    	tree.addTreeSelectionListener(treeListener);
    }
    
    public void setCellRender(TreeCellRenderer treeCellRenderer){
    	tree.setCellRenderer(treeCellRenderer);
    }
    
    public TreePath getSelectionPath(){
    	return tree.getSelectionPath();
    }
    
    public DefaultMutableTreeNode getSelectedNode(){
        TreePath currentSelection = tree.getSelectionPath();
        if (currentSelection != null) {
        	return (DefaultMutableTreeNode)(currentSelection.getLastPathComponent());
        }
        return null;
    }

    public DefaultMutableTreeNode getParentSelectedNode(){
        TreePath currentSelection = tree.getSelectionPath();
        if (currentSelection != null) {
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)
                         (currentSelection.getLastPathComponent());
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode)(currentNode.getParent());
            return parent;
        }
        return null;
    }

    /** Remove the currently selected node. */
    public void removeCurrentNode() {
        TreePath currentSelection = tree.getSelectionPath();
        if (currentSelection != null) {
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)
                         (currentSelection.getLastPathComponent());
            MutableTreeNode parent = (MutableTreeNode)(currentNode.getParent());
            if (parent != null) {// not allow remove root
                treeModel.removeNodeFromParent(currentNode);
                TreePath newPath = currentSelection.getParentPath();
                tree.setSelectionPath(newPath);
                return;
            }
        } 

        // Either there was no selection, or the root was selected.
        //toolkit.beep();
    }

    /** Add child to the currently selected node. */
    public DefaultMutableTreeNode addObject(Object child) {
        DefaultMutableTreeNode parentNode = null;
        TreePath parentPath = tree.getSelectionPath();

        if (parentPath == null) {
            parentNode = rootNode;
        } else {
            parentNode = (DefaultMutableTreeNode)
                         (parentPath.getLastPathComponent());
        }

        return addObject(parentNode, child, true);
    }

    public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent,
                                            Object child) {
        return addObject(parent, child, false);
    }

    public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent,
                                            Object child, 
                                            boolean shouldBeVisible) {
        DefaultMutableTreeNode childNode = 
                new DefaultMutableTreeNode(child);

        if (parent == null) {
            parent = rootNode;
        }
	
	//It is key to invoke this on the TreeModel, and NOT DefaultMutableTreeNode
        treeModel.insertNodeInto(childNode, parent, 
                                 parent.getChildCount());

        //Make sure the user can see the lovely new node.
        if (shouldBeVisible) {
            tree.scrollPathToVisible(new TreePath(childNode.getPath()));
        }
        return childNode;
    }
    
    
    
    public Vector getNodesAtLevel(int level){
    	Vector nodesAtLevel = null;
        if (treeModel != null) {
        	
        	nodesAtLevel = new Vector();
        	
            Object root = treeModel.getRoot();
            
            if (level==1){
            	nodesAtLevel.add(root);
            } else {
            	findNodesAtLevel(treeModel, root, level, 1, nodesAtLevel);
            }
            
        	return nodesAtLevel;
        }
        else {
           return null;
        }    	
    }
    
    private void findNodesAtLevel(TreeModel model, Object o, int levelToFind, int levelTraversing, Vector resultNodes){
        int  cc;
        cc = model.getChildCount(o);
        for( int i=0; i < cc; i++) {
          Object child = model.getChild(o, i );
          if (levelToFind==levelTraversing+1){
        	  resultNodes.add(child);
          }
          if (!model.isLeaf(child)){
          	findNodesAtLevel(model, child, levelToFind, levelTraversing+1, resultNodes); 
          }
         }
    }

    public Vector getChildsOfNodes(DefaultMutableTreeNode node){
    	Vector nodes = new Vector();
        int  cc;
        cc = treeModel.getChildCount(node);
        for( int i=0; i < cc; i++) {
          Object child = treeModel.getChild(node, i );
          nodes.add(child);
         }
        return nodes;
    }   
    
    public void traverse() { 
        if (treeModel != null) {
            Object root = treeModel.getRoot();
            System.out.println(root.toString());
            walk(treeModel,root);    
            }
        else
           System.out.println("Tree is empty.");
        }
        
      protected void walk(TreeModel model, Object o){
        int  cc;
        cc = model.getChildCount(o);
        for( int i=0; i < cc; i++) {
          Object child = model.getChild(o, i );
          if (model.isLeaf(child))
            System.out.print(child.toString()+"\t");
          else {
            System.out.print(child.toString()+"--");
            walk(model,child ); 
            }
          if (i==cc-1){
        	  System.out.println();
          }
         }
       } 

      public DefaultMutableTreeNode getParentNode(String childNameToFind){
          if (treeModel != null) {
              Object root = treeModel.getRoot();
              return findParent(treeModel,root, childNameToFind);    
          }
          else {
             return null;
          }
      }
      
      private DefaultMutableTreeNode findParent(TreeModel model, Object o, String childNameToFind){
          int  cc;
          cc = model.getChildCount(o);
          for( int i=0; i < cc; i++) {
            Object child = model.getChild(o, i );
            if (childNameToFind.equals(child.toString())){
            	return (DefaultMutableTreeNode)o;
            }
            if (!model.isLeaf(child)){
            	return findParent(model,child, childNameToFind ); 
            }
           }
          
          return null;
          
         } 

      public int getLevelOfNode(DefaultMutableTreeNode node){
          if (treeModel != null) {
              Object root = treeModel.getRoot();
              return findNodeLevel(treeModel,root, node, 1);    
          }
          else {
             return -1;
          }
      }

      public static int getLevelOfNode(JTree tree, DefaultMutableTreeNode node){
    	  TreeModel treeModel = tree.getModel();
          if (treeModel != null) {
              Object root = treeModel.getRoot();
              return findNodeLevel(treeModel,root, node, 1);    
          }
          else {
             return -1;
          }
      }

      private static int findNodeLevel(TreeModel model, Object o, DefaultMutableTreeNode node, int level){
          if (o==node){
        	  return level;
          }
          
          int  cc;
         
          cc = model.getChildCount(o);
          for( int i=0; i < cc; i++) {
            Object child = model.getChild(o, i );
            //if (!model.isLeaf(child)){
            	int result = findNodeLevel(model,child, node, level+1 );
            	if (result != -1) {
            		return result;
            	}
            //}
           }
          
          return -1;
          
         } 
        	
}