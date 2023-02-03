package fr.eni.eniD2WM147.Servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.taglibs.standard.tag.common.fmt.RequestEncodingSupport;

import fr.eni.eneDW2M147.businessException.BusinessException;
import fr.eni.eniD2WM147.bll.EnchereManager;
import fr.eni.eniD2WM147.bo.ArticleVendu;
import fr.eni.eniD2WM147.bo.Categorie;
import fr.eni.eniD2WM147.bo.Utilisateur;

/**
 * Servlet implementation class ServletAfficherArticle
 */
@WebServlet("/CreationArticle")
public class ServletCreationArticle extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("Creation Vente - doGet");
		EnchereManager em = new EnchereManager();
		
		List<Categorie> categories = new ArrayList<>();
		try {
			categories = em.selectAllCat();
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		request.setAttribute("categories", categories);

		RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/JSP/CreationArticle.jsp");
		rd.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("doPost");
		
		try {
		EnchereManager em = new EnchereManager();
		ArticleVendu article = null;
		
		String art = request.getParameter("article");
		String description =request.getParameter("story");
		String categorie =request.getParameter("listcate");
		String prix =request.getParameter("miseAprix");
		String debutVente = request.getParameter("debutEnchere");
		String finVente =request.getParameter("finEnchere");
		String rue= request.getParameter("rue");
		String codePostal=request.getParameter("codePostal");
		String ville =request.getParameter("ville");

		request.getParameter("saveNewArt");
		article = em.insert(article);
		request.getParameter("annulerNewArt");

	
		} catch (BusinessException e) {
			e.printStackTrace();
			response.sendRedirect(request.getContextPath()+"/accueil");
			doGet(request, response);
		}

	}

}
