package main;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * TreeControler class is responsible for collecting data from JSP file, processing on them and passing new data to JSP file.
 * @author Mansoureh Aghabeig
 *
 */
@WebServlet("/TreeControler")
public class TreeControler extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public TreeControler() {

	}

	/**
	 * Return an asked node with a specific id.
	 * 
	 * @param id of asked node
	 * @return a node tree 
	 */
	private Tree getNodeTree(int id){
		SessionFactory hibernateFactory = Tree.factoryHibernateGenerator();
		Session session = hibernateFactory.getCurrentSession();
		ArrayList <Tree> theTrees;
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
	 * Handles delete and add operation and send the new tree structure to JSP file.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("action");
		System.out.println(action);
		int id = Integer.parseInt(request.getParameter("id"));
		Tree tree = getNodeTree(id);
		if(action.equalsIgnoreCase("add")){
			tree.addChildNode();
		}
		else if (action.equalsIgnoreCase("delete")){
			tree.deleteChildNode();
		}
		else if (action.equalsIgnoreCase("move")){
			int idParent = Integer.parseInt(request.getParameter("idParent"));
			tree.moveChildNode(idParent);
		}
		response.sendRedirect("index.jsp");
	}
	
	/**
	 * Handles Update content of node tree and send the new tree structure to the JSP file.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Tree tree = new Tree();
		int id = Integer.parseInt(request.getParameter("id"));
		int content = Integer.parseInt(request.getParameter("content"));
		tree.setId(id);
		tree.updateChildNode(content);
		response.sendRedirect("index.jsp");
	}

}
