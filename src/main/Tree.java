package main;
import java.awt.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Tree class is responsible for:
 *  <li> Handles the database connection to MySQL server </li>
 *  <li> Contains the logic for  database operation </li>
 *  <li> Generates HTML string for web design </li>
 * 
 * @author Mansoureh Aghabeig 
 *
 */
@Entity
@Table(name = "tree")
public class Tree {
 
	/**
	 * Define hibernate configuration file for generating hibernate session factory.
	 */
	public static String HIBERNATE_CONFIG = "hibernate.cfg.xml";

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)

	/**
	 * id property which map to id property in database.
	 */
	@Column(name = "id")
	private int id;

	/**
	 * content property which map to content property in database.
	 */
	@Column(name = "content")
	private int content;

	/**
	 * idParent property which map to idparent property in database.
	 */
	@Column(name = "idParent")
	private int idParent;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getContent() {
		return content;
	}

	public void setContent(int content) {
		this.content = content;
	}

	public int getIdParent() {
		return idParent;
	}

	public void setIdParent(int idParent) {
		this.idParent = idParent;
	}

	/**
	 * This constructor should really only be used internally by Hibernate.
	 * 
	 */
	public Tree(){

	}

	/**
	 * Return root rode object. (If there has already been a root node in table of database returns it, unless generates a root node in table of database and returns it).
	 * 
	 * @param content defines the content value of the root node
	 */
	public Tree (int content) {
		Tree rootNode = getRootNode();
		if (rootNode != null){
			this.content = rootNode.getContent();
			this.idParent = rootNode.getIdParent();
			this.id = rootNode.getId();
		} else {
			this.idParent = -1;
			this.content = content;
			saveNode();
		}
	}

	/**
	 * Generate a tree object and save it in database (a node in the tree) with input parameters.
	 * 
	 * @param content defines content value of the tree node
	 * @param idParent defines parent id of the tree node
	 */
	private Tree(int content, int idParent) {
		this.content = content;
		this.idParent = idParent;
		saveNode();
	}

	/**
	 * Save current node to the database.
	 * 
	 */
	private void saveNode() {
		SessionFactory hibernateFactory = factoryHibernateGenerator();
		Session session = hibernateFactory.getCurrentSession();
		try {
			session.beginTransaction();
			session.save(this);
			session.getTransaction().commit();
		} finally {
			hibernateFactory.close();
		}
	}

	/** 
	 * Generate a Hibernate session factory.
	 * 
	 * @return Hibernate session factory
	 */
	public static SessionFactory factoryHibernateGenerator() {
		SessionFactory factory = new Configuration()
				.configure(HIBERNATE_CONFIG)
				.addAnnotatedClass(Tree.class)
				.buildSessionFactory();
		return factory;
	}

	/**
	 * Return child nodes of the current node tree.
	 * 
	 * @return child nodes
	 */
	private ArrayList<Tree> getChildNodes(){
		SessionFactory hibernateFactory = factoryHibernateGenerator();
		Session session = hibernateFactory.getCurrentSession();
		ArrayList<Tree> theTrees;
		try {
			session.beginTransaction();
			theTrees = (ArrayList<Tree>) session.createQuery("from Tree s where s.idParent = " + this.id).list();
			session.getTransaction().commit();
		} finally {
			hibernateFactory.close();
		}
		return theTrees;
	}
	/**
	 * Return root node of the tree structure from database.
	 * 
	 * @return root node of the tree
	 */
	private Tree getRootNode(){
		SessionFactory hibernateFactory = factoryHibernateGenerator();
		Session session = hibernateFactory.getCurrentSession();
		ArrayList <Tree> theTrees;
		try {
			session.beginTransaction();
			theTrees = (ArrayList<Tree>) session.createQuery("from Tree s where s.idParent = " + -1).list();
			session.getTransaction().commit();
		} finally {
			hibernateFactory.close();
		}
		if(theTrees.isEmpty()){
			return null;
		}
		return theTrees.get(0);
	}

	/**
	 * Return a node tree from tree structure.
	 * 
	 * @param id defines id of the asked node tree
	 * @return the node tree, or if the node tree does not exist, return null object
	 */
	private Tree getNodeTree(int id){
		SessionFactory hibernateFactory = factoryHibernateGenerator();
		Session session = hibernateFactory.getCurrentSession();
		ArrayList<Tree> theTrees;
		try {
			session.beginTransaction();
			theTrees = (ArrayList<Tree>) session.createQuery("from Tree s where id = " + id).list();
			session.getTransaction().commit();
		} finally {
			hibernateFactory.close();
		}
		if(theTrees.isEmpty()){
			return null;
		}
		return theTrees.get(0);
	}

	/**
	 * Add a node tree (a child node) to the current node tree.
	 * 
	 * @param content defines the content value of added node (child node)
	 * @return
	 */
	public Tree addChildNode(){
		int valueContent = this.content + 1;
		Tree tree = getNodeTree(this.getIdParent());
		while (tree != null){
			valueContent += tree.content;
			tree = getNodeTree(tree.getIdParent());
		}
		return new Tree(valueContent, this.id);
	}

	/** 
	 * Delete the current node (with its all child nodes) from database.
	 */
	public void deleteChildNode(){
		SessionFactory hibernateFactory = factoryHibernateGenerator();
		Session session = hibernateFactory.getCurrentSession();
		try {
			session.beginTransaction();
			ArrayList <Tree> theTrees = getChildNodes();
			for (Tree theTree:theTrees){
				theTree.deleteChildNode();
			}
			session.createQuery("delete from Tree where id ="  +this.id)
			.executeUpdate();
			session.getTransaction().commit();
		} finally {
			hibernateFactory.close();
		}
	}

	/**
	 * Calculate the sum of values of all nodes above asked node.
	 * 
	 * @param tree defines asked node
	 * @return sum of values of all nodes above asked node
	 */
	private int getSumOfValuesAboveMe(Tree tree ) {

		if (!tree.isRoot()) {
			Tree treeParent = tree.getNodeTree(tree.idParent);
			return tree.getContent() + tree.getSumOfValuesAboveMe(treeParent);
		} else {
			return tree.content;
		}
	}

	/**
	 * Update content of a node tree that is not leaf.
	 * 
	 * @param  content defines the new content value of current node
	 * @return Return node tree with new content value
	 */
	public Tree updateChildNode(int content){
		SessionFactory hibernateFactory = factoryHibernateGenerator();
		Session session = hibernateFactory.getCurrentSession();
		try {
			Tree tree = this.getNodeTree(this.getIdParent());
			int value;
			if (tree == null){
				value = content;	
			}
			else {
				value = getSumOfValuesAboveMe(tree) + content + 1;
			}
			this.setContent(value);
			session.beginTransaction();
			session.createQuery("update Tree set content = " + value + "where id ="  +this.id)
			.executeUpdate();
			session.getTransaction().commit();
			ArrayList <Tree> theTrees = getChildNodes();
			for (Tree theTree:theTrees){
				theTree.updateChildNode(0);
			}

		} finally {
			hibernateFactory.close();
		}
		return this;
	}

	/**
	 *  Change Parent id of current node (moving current node).
	 *  
	 * @param idParent defines id of new parent for current node
	 */
	public void moveChildNode(int idParent){
		//check this move is legal
		if (IsLegalMove(idParent) == 0){
			SessionFactory hibernateFactory = factoryHibernateGenerator();
			Session session = hibernateFactory.getCurrentSession();
			try {
				session.beginTransaction();
				session.createQuery("update Tree set idParent = " + idParent + "where id ="  +this.id)
				.executeUpdate();
				session.getTransaction().commit();
			} finally {
				hibernateFactory.close();
			}

			Tree tree = this.getRootNode();
			tree.updateChildNode(tree.getContent());
		}
	}

	/**
	 * Check if the current node can be placed under the chosen parent node (a parent can not moved under its child)
	 * 
	 * @param idParent defines id of new parent for moving current node under it.
	 */
	private int IsLegalMove(int idParent) {
		ArrayList <Tree> theTrees = getChildNodes();
		int value = 0;
		for (Tree theTree:theTrees){
			value += theTree.IsLegalMove(idParent);
		}
		if (this.id == idParent){
			value = 1;
		}
		return value;
	}

	/**
	 * Generate HTML String 
	 * 
	 * @param childHTMLString contains HTML string of the sub tree of current node
	 * @return HTML String
	 */
	private String HTMLStringMaker(String childHTMLString){
		return  "<ul>"+
				"<li>"+this.getContent()+ "&nbsp;" + "<span onclick=\"RedirectForm("+this.id+")\">Add</span>"+ 
				"&nbsp;" +  
				(this.isLeaf() ? "" : "<span  onclick=\"ShowForm("+this.id+",\'update\')\">update</span>" ) + 
				"&nbsp;" + 
				"<span onclick=\"GetConfirmation("+this.id+")\">Delete</span>" +
				"&nbsp;" + 
				"<span onclick=\"StartMove("+this.id+")\">Move</span>" +
				"&nbsp;" + 
				"<span class=SetElement style=visibility: hidden onclick=\"FinishMove("+this.id+")\">Set</span>" +
				childHTMLString+
				"</ul>";
	}

	/**
	 * Generate HTML string for visualizing tree structure on JSP file.
	 * 
	 * @return HTML String
	 */
	public String toHTML() {
		String childHTMLString ="";
		String resutlhtml;
		ArrayList <Tree> theTrees = getChildNodes();
		if (theTrees.isEmpty()){
			resutlhtml = HTMLStringMaker(childHTMLString);
			return resutlhtml;
		}

		for (Tree theTree:theTrees){
			childHTMLString += theTree.toHTML();
		}	
		resutlhtml = HTMLStringMaker(childHTMLString);
		return resutlhtml;
	}

	/**
	 * Check if current node is a leaf node.
	 * 
	 * @return true if current node is a leaf node
	 */
	private boolean isLeaf() {
		ArrayList <Tree> theTrees = getChildNodes();
		if (theTrees.isEmpty()){
			return true;
		}
		return false;
	}

	/**
	 * Check if current node it root node.
	 * 
	 * @return true if current node is root node
	 */
	private boolean isRoot() {
		if (this.idParent == -1){
			return true;
		}
		return false;
	}
	 /**
	  * Return HTML String to JSP File.
	  * 
	  * @return HTML String
	  */
	public static final String callingJSPFile() {
		Tree tree = new Tree(0);
      	return ""+tree.toHTML();
	}

}



