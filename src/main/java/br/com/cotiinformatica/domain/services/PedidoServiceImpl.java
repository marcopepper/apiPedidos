package br.com.cotiinformatica.domain.services;

import java.util.UUID;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import br.com.cotiinformatica.domain.dtos.requests.PedidoRequest;
import br.com.cotiinformatica.domain.dtos.responses.PedidoResponse;
import br.com.cotiinformatica.domain.entities.OutboxMessage;
import br.com.cotiinformatica.domain.entities.Pedido;
import br.com.cotiinformatica.domain.exceptions.PedidoNaoCriadoException;
import br.com.cotiinformatica.domain.exceptions.PedidoNaoEncontradoException;
import br.com.cotiinformatica.domain.interfaces.PedidoService;
import br.com.cotiinformatica.repositories.OutboxMessageRepository;
import br.com.cotiinformatica.repositories.PedidoRepository;

@Service
public class PedidoServiceImpl implements PedidoService {
	@Autowired
	public PedidoRepository pedidoRepository;

	@Autowired
	public OutboxMessageRepository outboxMessageRepository;

	@Autowired
	public PlatformTransactionManager transactionManager;

	@Autowired
	public ModelMapper modelMapper;
	
	@Autowired
	public ObjectMapper objectMapper;

	@Override
	public PedidoResponse criar(PedidoRequest request) {

		var pedido = modelMapper.map(request, Pedido.class);

		var transaction = new TransactionTemplate(transactionManager);
		transaction.executeWithoutResult(status -> {
			try {

				pedidoRepository.save(pedido);
				var outboxMessage = new OutboxMessage();
				outboxMessage.setTipoEvento("pedido_criado");
				outboxMessage.setPayload(objectMapper.writeValueAsString(pedido));

				outboxMessageRepository.save(outboxMessage);
			} catch (Exception e) {
				e.printStackTrace();
				status.setRollbackOnly(); // desfazer a transação
				throw new PedidoNaoCriadoException(); // lançar uma exceção
			}
		});

		return modelMapper.map(pedido, PedidoResponse.class);
	}

	@Override
	public PedidoResponse alterar(UUID id, PedidoRequest request) {

		var pedido = pedidoRepository.findById(id).orElseThrow(() -> new PedidoNaoEncontradoException(id));

		modelMapper.map(request, pedido);
		pedidoRepository.save(pedido);
		return modelMapper.map(pedido, PedidoResponse.class);
	}

	@Override
	public PedidoResponse excluir(UUID id) {
		var pedido = pedidoRepository.findById(id).orElseThrow(() -> new PedidoNaoEncontradoException(id));

		pedidoRepository.delete(pedido);
		return modelMapper.map(pedido, PedidoResponse.class);
	}

	@Override
	public Page<PedidoResponse> consultar(Pageable pageable) {

		var pedidos = pedidoRepository.findAll(pageable);
		return pedidos.map(pedido -> modelMapper.map(pedido, PedidoResponse.class));
	}

	@Override
	public PedidoResponse obter(UUID id) {
		var pedido = pedidoRepository.findById(id).orElseThrow(() -> new PedidoNaoEncontradoException(id));

		return modelMapper.map(pedido, PedidoResponse.class);
	}
}
