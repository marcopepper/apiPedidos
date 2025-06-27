package br.com.cotiinformatica.domain.exceptions;

import java.text.MessageFormat;
import java.util.UUID;

public class PedidoNaoEncontradoException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private UUID pedidoID;

	public PedidoNaoEncontradoException(UUID pedidoId) {
		this.pedidoID = pedidoId;
	}
	
	@Override
	public String getMessage() {
		return MessageFormat.format("O pedido ''{0}'' não foi encontrado", pedidoID);
	}
	
	

}
