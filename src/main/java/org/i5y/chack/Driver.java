package org.i5y.chack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicInteger;

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
	
	public static AtomicInteger amount = new AtomicInteger(0);

	public static AtomicInteger itemsUsed = new AtomicInteger(0);

	public static class DataSource extends HttpServlet {

		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			resp.setContentType("application/json");
			resp.addHeader("Cache-Control", "no-store, max-age=0");
			resp.getWriter().write("{");
			resp.getWriter().write("\"amount\":"+amount.get()+",");
			resp.getWriter().write("\"items_used\":"+itemsUsed.get());
			resp.getWriter().write("}");
		}
	}

	public static class DataUpload extends HttpServlet {

		@Override
		protected void doPost(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			int increment = Integer.parseInt(req.getParameter("count"));
			itemsUsed.addAndGet(increment);
		}
	}
	
	public static class PaypalButton extends HttpServlet {

		 final String FILE; 
		
		public PaypalButton() throws Exception{
			BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("button.html")));
			
			String line = br.readLine();
			String file = "";
			while(line != null){
				file+=line;
				line = br.readLine();
			}
			FILE = file;
		}
		
		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			resp.setContentType("text/html");
			resp.addHeader("Cache-Control", "no-store, max-age=0");
			resp.getWriter().write(FILE);
		}
	}

	public static class PaypalCallback extends HttpServlet {

		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			
			String authToken = "ZjmIRL-WoHTIP6XvX2Ika6RUXAL3vkCjMCVYVPhI7VYaWaaF2P7CvEAB2WC";
			String tx = req.getParameter("tx");
			String amt = req.getParameter("amt");
			amount.addAndGet((int)(Double.parseDouble(amt)*100));
			resp.getWriter().write("");
		}
	}
	
	public static void main(String[] args) throws Exception {
		Server server = new Server(9777);

		ServletContextHandler context = new ServletContextHandler(
				ServletContextHandler.SESSIONS);
		context.setContextPath("/");

		context.addServlet(new ServletHolder(new PaypalButton()),
				"/button");
		context.addServlet(new ServletHolder(new DataSource()),
				"/data");
		context.addServlet(new ServletHolder(new DataUpload()),
				"/data-update");
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
