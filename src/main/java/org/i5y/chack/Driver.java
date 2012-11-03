package org.i5y.chack;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class Driver {
	public static class PaypalButton extends HttpServlet {

		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			resp.setContentType("application/json");
			resp.addHeader("Cache-Control", "no-store, max-age=0");
			resp.getWriter().write("");
		}
	}

	public static class PaypalCallback extends HttpServlet {

		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			
			String authToken = "6FoqrNh1cODczB9r9xBrV2iD7t8Jn25saE0b2oCkcjvcd7b0fmfU_AyWPqK";
			String tx = req.getParameter("tx");
			
			resp.setContentType("application/json");
			resp.addHeader("Cache-Control", "no-store, max-age=0");
			resp.getWriter().write("");
		}
	}
	
	public static void main(String[] args) throws Exception {
		Server server = new Server(9000);

		ServletContextHandler context = new ServletContextHandler(
				ServletContextHandler.SESSIONS);
		context.setContextPath("/");

		context.addServlet(new ServletHolder(new PaypalButton()),
				"/.well-known/browserid");
		context.addServlet(new ServletHolder(new PaypalCallback()),
				"/paypalCallback");

		ServletHolder defaultServletHolder = new ServletHolder(
				new DefaultServlet());
		defaultServletHolder.setInitParameter("resourceBase", "target/classes");
		context.addServlet(defaultServletHolder, "/");

		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { context });
		server.setHandler(handlers);

		server.start();
		server.join();
	}
}
