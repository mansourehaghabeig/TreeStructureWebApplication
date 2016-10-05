package test;
import static org.junit.Assert.*;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.Before;
import org.junit.Test;

import main.Tree;

import java.sql.*;
import java.util.ArrayList;
public class JunitTestTree {
	/**
	 * JunitTestTree class is responsible for doing complete test on CRUD operations.
	 * 
	 * @author Mansoureh Aghabeig 
	 * 
	 */

	Tree rootNode = null;
	Tree leafNode = null;
	Tree subTree  = null;

	/**
	 * <li> This part will be run before running each test method. </li>
	 * <li> It generate a test table in database. </li> 
	 * <li> It fills table with an initial structure. </li>
	 */
	@Before
	public void before() {
		Tree.HIBERNATE_CONFIG = "hibernate.cfg.test.xml";
		String dbUrl = "jdbc:mysql://localhost:3301/testdatabase?useSSL=false";
		String user = "root";
		String pass = "1234";
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = DriverManager.getConnection(dbUrl, user, pass);
			stmt =  conn.createStatement();
			try {
				String sql = "drop table testdatabase.tree"; 
				stmt.executeUpdate(sql);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			//create table tree and fill it with a structure		
			String sql = "create table `testdatabase`.`tree` (`id` int not null auto_increment,`content` int null,`idparent` int null, primary key (`id`))";
			stmt.executeUpdate(sql);
			sql = "alter table testdatabase.tree auto_increment = 1";
			stmt.executeUpdate(sql);
			int[][] value = new int[][]{{0,-1},{1,1},{1,1},{2,2},{2,2},{2,3},{2,3},{2,3}};
			for (int i = 0; i <8; i++){
				sql = "insert into testdatabase.tree (content,idparent) values ("+ value[i][0]+","+ value[i][1]+")";
				stmt.executeUpdate(sql);
			}

			//get root, leaf and a subtree node from table tree
			sql = "select * from testdatabase.tree where id = 3";
			stmt.executeQuery(sql);
			ResultSet rs = stmt.executeQuery(sql);
			rs.next();
			subTree = new Tree();
			subTree.setId(rs.getInt("id"));
			subTree.setIdParent(rs.getInt("idparent"));
			subTree.setContent(rs.getInt("content"));

			sql = "select * from testdatabase.tree where id = 8";
			stmt.executeQuery(sql);
			rs = stmt.executeQuery(sql);
			rs.next();
			leafNode = new Tree();
			leafNode.setId(rs.getInt("id"));
			leafNode.setIdParent(rs.getInt("idparent"));
			leafNode.setContent(rs.getInt("content"));

			sql = "select * from testdatabase.tree where id = 1";
			stmt.executeQuery(sql);
			rs = stmt.executeQuery(sql);
			rs.next();
			rootNode = new Tree();
			rootNode.setId(rs.getInt("id"));
			rootNode.setIdParent(rs.getInt("idparent"));
			rootNode.setContent(rs.getInt("content"));

		} catch(SQLException se){
			//Handle errors for JDBC
			se.printStackTrace();
		} catch(Exception e){
			//Handle errors for Class.forName
			e.printStackTrace();
		} finally {
			//finally block used to close resources
			try {
				if(stmt!=null)
					conn.close();
			} catch(SQLException se){
			}// do nothing
			try {
				if(conn!=null)
					conn.close();
			} catch(SQLException se){
				se.printStackTrace();
			}//end finally try
		}//end try
	}

	/**
	 * Return the tree node with a specific id value.
	 * @return tree node
	 */
	public static Tree getTree(int id){
		SessionFactory hibernateFactory = Tree.factoryHibernateGenerator();
		Session session = hibernateFactory.openSession();
		ArrayList <Tree> theTrees;
		try {
			session.beginTransaction();
			theTrees = (ArrayList<Tree>) session.createQuery("from Tree s where s.id = " + id).list();

			session.getTransaction().commit();
		} finally { 
			hibernateFactory.close();
		}
		if(theTrees.isEmpty()){
			return null;
		}

		return theTrees.get(0);
	}

	//-----------------------Testing Add Method-----------------------
	/**
	 * Testing adding a node to leaf node in tree structure
	 */
	@Test
	public void testAddToLeafNode() {
		Tree tree = leafNode.addChildNode();
		Tree treeOfDatabase = getTree(tree.getId());
		assertNotNull(treeOfDatabase.getId());
		assertEquals(treeOfDatabase.getIdParent(),leafNode.getId());
		assertEquals(treeOfDatabase.getContent(),4);
	}
	/**
	 * Testing adding a node to a sub tree node in tree structure
	 */
	@Test
	public void testAddToSubTreeNode() {
		Tree tree = subTree.addChildNode();
		Tree treeOfDatabase = getTree(tree.getId());
		assertNotNull(treeOfDatabase .getId());
		assertEquals(treeOfDatabase .getIdParent(),subTree.getId());
		assertEquals(treeOfDatabase .getContent(),2);
	}
	/**
	 * Testing adding root node to tree
	 */
	@Test
	public void testAddRootNode() {
		rootNode.deleteChildNode();
		Tree tree = new Tree(0);
		Tree treeOfDatabase = getTree(tree.getId());
		assertNotNull(treeOfDatabase.getId());
		assertEquals(treeOfDatabase.getIdParent(),-1);
		assertEquals(treeOfDatabase.getContent(),0);
	}

	//-----------------------Testing Delete Method-----------------------
	/**
	 * Testing a leaf node from tree structure
	 */
	@Test
	public void testDeleteLeafNode() {
		leafNode.deleteChildNode();
		assertNull(getTree(leafNode.getId()));
	}
	/**
	 * Testing delete a sub tree node from tree structure
	 */
	@Test
	public void testDeleteSubTreedNode() {
		subTree.deleteChildNode();
		assertNull(getTree(subTree.getId()));
	}
	/**
	 * Testing root node from tree structure
	 */
	@Test
	public void testDeleteRootNode() {
		rootNode.deleteChildNode();
		assertNull(getTree(rootNode.getId()));
	}

	//-----------------------Testing Update Method-----------------------

	/**
	 * Testing updating value of a leaf node in tree structure
	 */
	@Test
	public void testUpdateLeafNode() {
		leafNode.updateChildNode(7);
		Tree treeOfDatabase = getTree(leafNode.getId());
		assertEquals(treeOfDatabase.getContent(),leafNode.getContent());
	}
	/**
	 * Testing updating value of a sub tree node in tree structure
	 */
	@Test
	public void testUpdateSubTreeNode() {
		subTree.updateChildNode(7);
		Tree treeOfDatabase = getTree(subTree.getId());
		assertEquals(treeOfDatabase.getContent(),8);
		
	}
	/**
	 * Testing updating value of root node in tree structure
	 */
	@Test
	public void testUpdateRootNode() {
		rootNode.updateChildNode(7);
		Tree treeOfDatabase = getTree(rootNode.getId());
		assertEquals(treeOfDatabase.getContent(),7);
	}

	//-----------------------Testing Move Method-----------------------
	/**
	 * Testing moving leaf node (leaf node can move to any position)
	 */
	@Test
	public void testMoveLeafNode() {
		leafNode.moveChildNode(2);
		Tree treeOfDatabase = getTree(leafNode.getId());
		assertEquals(treeOfDatabase.getIdParent(),2);
	}
	/**
	 * Testing moving sub tree to another node
	 */
	@Test
	public void testMoveSubTreeNode() {
		subTree.moveChildNode(2);
		Tree treeOfDatabase = getTree(subTree.getId());
		assertEquals(treeOfDatabase.getIdParent(),2);
	}
	/**
	 * Testing moving sub tree to its child node (this move is illegal, so the position of sub tree must not changed)
	 */
	@Test
	public void testMoveSubTreeNodeUnderItsChild() {
		subTree.moveChildNode(8);
		Tree treeOfDatabase = getTree(subTree.getId());
		assertEquals(treeOfDatabase.getIdParent(),subTree.getIdParent());
	}
	
	/**
	 * Testing moving root node (root node can not change its position)
	 */
	@Test
	public void testMoveRootNode() {
		rootNode.moveChildNode(3);
		Tree treeOfDatabase = getTree(rootNode.getId());
		assertEquals(treeOfDatabase.getIdParent(),-1);
	}
}
