package fr.eni.eniD2WM147.Servlets;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import fr.eni.eniD2WM147.bll.ArticleManager;
import fr.eni.eniD2WM147.bo.ArticleVendu;
import fr.eni.eniD2WM147.bo.Categorie;
import fr.eni.eniD2WM147.bo.Retrait;
import fr.eni.eniD2WM147.bo.Utilisateur;
import fr.eni.eniD2WM147.businessException.BusinessException;
import fr.eni.eniD2WM147.dal.ImageDaoJdbcImpl;

/**
 * Servlet implementation class ServletAfficherArticle
 */
@WebServlet("/CreationArticle")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
		maxFileSize = 1024 * 1024 * 10, // 10MB
		maxRequestSize = 1024 * 1024 * 50) // 50MB
public class ServletCreationArticle extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static final String SAVE_DIRECTORY = "uploads";

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("Creation Vente - doGet");

		ArticleManager am = ArticleManager.getInstance();
		request.setCharacterEncoding("UTF-8");

		List<Categorie> categories = new ArrayList<>();
		try {
			categories = ArticleManager.getInstance().selectAllCat();
		} catch (BusinessException e) {

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

		System.out.println("ServletCreationArticle - doPost");
		request.setCharacterEncoding("UTF-8");

		HttpSession session = request.getSession();
		

		ArticleVendu article = null;
		Retrait retrait = null;

		String nomArticle = request.getParameter("nomArticle");
		request.setAttribute("nomArticle", nomArticle);

		String description = request.getParameter("descritpion");
		request.setAttribute("descritpion", description);

		String image = request.getParameter("pictureFile");
		String appPath = request.getServletContext().getRealPath("");
		Part part = request.getPart("pictureFile");
		//String fileName = saveFile(appPath, part);

		System.out.println("COUCOU");
		// request.setAttribute("Utilisateur", image);

		String categorie = request.getParameter("listcate");
		System.out.println(categorie);
		request.setAttribute("listcate", categorie);

		String prix = request.getParameter("miseAprix");
		request.setAttribute("miseAprix", prix);

		String debutVente = request.getParameter("debutEnchere");
		request.setAttribute("debutEnchere", debutVente);

		String finVente = request.getParameter("finEnchere");
		request.setAttribute("finEnchere", finVente);

		String rue = request.getParameter("rue");

		request.setAttribute("rue", ((Utilisateur) session.getAttribute("Utilisateur")).getRue());

		String codePostal = request.getParameter("codePostal");

		request.setAttribute("codePostal", ((Utilisateur) session.getAttribute("Utilisateur")).getCodePostal());

		String ville = request.getParameter("ville");

		request.setAttribute("ville", ville);

		LocalDateTime dateDebut = null;
		LocalDateTime dateFin = null;
		int numCat = Integer.parseInt(categorie);

		dateDebut = LocalDateTime.parse(debutVente);
		dateFin = LocalDateTime.parse(finVente);

		int prixEntier = Integer.parseInt(prix);
		Utilisateur vendeur = (Utilisateur) session.getAttribute("Utilisateur");

		Categorie cat = new Categorie(numCat);

		try {

			BusinessException bE = new BusinessException();
			if (nomArticle.isBlank()) {
				bE.addMessage("L'article doit avoir un nom");
			}
			if (description.isBlank()) {
				bE.addMessage("L'article doit avoir une description. ");
			}
			if (debutVente.isBlank()) {
				bE.addMessage("La date de début de vente doit être précisée.");
			}

			retrait = new Retrait(rue, codePostal, ville);
			article = new ArticleVendu(nomArticle, description, dateDebut, dateFin, prixEntier, 0, appPath, "CR", vendeur,
					retrait, cat, null);
			retrait.setArticle(article);
			request.getParameter("saveNewArt");
			request.getParameter("annulerNewArt");

			System.out.println("servlet-article" + article + retrait);
			article = ArticleManager.getInstance().insert(article);
			retrait = ArticleManager.getInstance().insertRetrait(retrait);
			System.out.println("fin doPost");
			response.sendRedirect(request.getContextPath() + "/accueil");

		} catch (BusinessException e) {
			e.printStackTrace();
			request.setAttribute("listeErreur", e.getListeMessage());
			response.sendRedirect(request.getContextPath()+"/WEB-INF/JSP/CreationArticle.jsp");
			//doGet(request, response);
		}

	}

	private String saveFile(String appPath, Part part) throws IOException {

		appPath = appPath.replace('\\', '/');

		// The directory to save uploaded file
		String fullSavePath = null;
		if (appPath.endsWith("/")) {
			fullSavePath = appPath + SAVE_DIRECTORY;
		} else {
			fullSavePath = appPath + "/" + SAVE_DIRECTORY;
		}

		// Creates the save directory if it does not exists
		File fileSaveDir = new File(fullSavePath);
		if (!fileSaveDir.exists()) {
			fileSaveDir.mkdir();
		}

		String filePath = null;

		String fileName = extractFileName(part);
		System.out.println(fileName);
		String[] fn = fileName.split("(\\.)");
		fileName = fn[0];
		String ext = fn[(fn.length - 1)];
		if (!ext.isEmpty()) {
			// generate a unique file name
			UUID uuid = UUID.randomUUID();
			fileName = fileName + "_" + uuid.toString() + "." + ext;
			if (fileName != null && fileName.length() > 0) {
				filePath = fullSavePath + File.separator + fileName;
				System.out.println("Write attachment to file: " + filePath);
				// Write to file
				part.write(filePath);
			}
		}
		return fileName;

	}

	private String extractFileName(Part part) {
		String contentDisp = part.getHeader("content-disposition");
		String[] items = contentDisp.split(";");
		for (String s : items) {
			if (s.trim().startsWith("fileName")) {
				String clientFileName = s.substring(s.indexOf("=") + 2, s.length() - 1);
				clientFileName = clientFileName.replace("\\", "/");
				int i = clientFileName.lastIndexOf('/');
				return clientFileName.substring(i + 1);
			}
		}
		return null;
	}

}
