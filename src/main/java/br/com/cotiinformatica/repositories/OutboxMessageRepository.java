package br.com.cotiinformatica.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import br.com.cotiinformatica.domain.entities.OutboxMessage;

@Repository
public interface OutboxMessageRepository extends JpaRepository<OutboxMessage, Integer> {
	/*
	 * Consulta para retornar apenas outboxes não enviados (enviado = false) Versão
	 * 'Query Methods'
	 */
	List<OutboxMessage> findByEnviadoFalseAndTipoEvento(String tipoEvento);

	/*
	 * Consulta para retornar apenas outboxes não enviados (enviado = false) Versão
	 * 'JPQL'
	 */
	@Query("""
			SELECT om FROM OutboxMessage om
			WHERE om.enviado = false
			  AND om.tipoEvento = :tipoEvento
			""")
	List<OutboxMessage> findByNaoEnviadosPorTipo(
			@Param("tipoEvento") String tipoEvento
			);
}
