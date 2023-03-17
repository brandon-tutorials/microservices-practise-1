package com.formacionbdi.springboot.app.zuul.filters;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

@Component
public class PostTiempoTransacurridoFilter extends ZuulFilter{

	private static Logger log = LoggerFactory.getLogger(PostTiempoTransacurridoFilter.class);
	
	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public Object run() throws ZuulException {
		
		RequestContext ctx = RequestContext.getCurrentContext();
		HttpServletRequest request  = ctx.getRequest();
		log.info("Entrando a post filter");
		Long tiempoInicio = (Long) request.getAttribute("tiempoInicio");
		Long tiempoFinal = System.currentTimeMillis();
		Long tiempoTransacurrido = tiempoInicio - tiempoFinal;
		log.info(String.format("Tiempo transcurrido en segundos %s seg.", tiempoTransacurrido.doubleValue()/1000.00));
		log.info(String.format("Tiempo transcurrido en segundos %s ms.", tiempoTransacurrido));

		return null;
	}

	@Override
	public String filterType() {
		// TODO Auto-generated method stub
		return "post";
	}

	@Override
	public int filterOrder() {
		// TODO Auto-generated method stub
		return 1;
	}

}
