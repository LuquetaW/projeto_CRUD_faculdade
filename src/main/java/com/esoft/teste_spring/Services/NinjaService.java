package com.esoft.teste_spring.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.esoft.teste_spring.DTOs.NinjaDTO;
import com.esoft.teste_spring.Exceptions.NaoEncontradoException;
import com.esoft.teste_spring.models.Jutsu;
import com.esoft.teste_spring.models.Missao;
import com.esoft.teste_spring.models.Ninja;
import com.esoft.teste_spring.models.Vila;
import com.esoft.teste_spring.repositories.JutsuRepository;
import com.esoft.teste_spring.repositories.MissaoRepository;
import com.esoft.teste_spring.repositories.NinjaRepository;
import com.esoft.teste_spring.repositories.VilaRepository;

import jakarta.transaction.Transactional;

@Service
public class NinjaService {

    private final NinjaRepository ninjaRepository;
    private final MissaoRepository missaoRepository;
    private final VilaRepository vilaRepository;
    JutsuRepository jutsuRepository;

    public NinjaService(NinjaRepository ninjaRepository, MissaoRepository missaoRepository, VilaRepository vilaRepository, JutsuRepository jutsuRepository) {
        this.ninjaRepository = ninjaRepository;
        this.missaoRepository = missaoRepository;
        this.vilaRepository = vilaRepository;
        this.jutsuRepository = jutsuRepository;
    }

    public List<NinjaDTO> listar() {
        return ninjaRepository.findAll().stream().map(ninja -> new NinjaDTO(ninja)).toList();
    }

    public NinjaDTO salvar(NinjaDTO ninja) {

        Ninja ninjaEntity = new Ninja(ninja);

        if (ninja.vilaId() != null) {
            Optional<Vila> vila = vilaRepository.findById(ninja.vilaId());
            if (vila.isPresent()) {
                ninjaEntity.setVila(vila.get());
            } else {
                throw new NaoEncontradoException("Vila com id " + ninja.vilaId() + " não foi encontrada!");
            }
        }

        if (ninja.missaoId() != null) {
            Missao missao = missaoRepository.findById(ninja.missaoId()).orElseThrow(
                    () -> new NaoEncontradoException("Missão com id " + ninja.missaoId() + " não foi encontrada!"));
            ninjaEntity.setMissao(missao);
        }

        return new NinjaDTO(ninjaRepository.save(ninjaEntity));
    }

    @Transactional
    public NinjaDTO salvar(Long id, NinjaDTO ninja) {
        ninjaRepository.findById(id)
                .orElseThrow(() -> new NaoEncontradoException("Ninja com o id " + id + " não foi encontrado!"));

        Ninja ninjaEntity = new Ninja(ninja);
        ninjaEntity.setId(id);

        if (ninja.missaoId() != null) {
            Missao missao = missaoRepository.findById(ninja.missaoId()).orElse(null);
            ninjaEntity.setMissao(missao);
        }

        if (ninja.vilaId() != null) {
            Vila vila = vilaRepository.findById(ninja.vilaId()).orElse(null);
            ninjaEntity.setVila(vila);
        }

        // if (ninja.jutsuIds() != null) {
        //     List<Jutsu> listaJustus = jutsuRepository.findById(id)//Continuar, estou pegando a lista de ids que não veio nulo e ?? buscando pra ver se existe no repositório de jutsus ? se sim eu adiciono esse jutsu a esse ninja?
        // }

        return new NinjaDTO(ninjaRepository.save(ninjaEntity));
    }

    public NinjaDTO buscarPorId(Long id) {
        return new NinjaDTO(ninjaRepository.findById(id)
                .orElseThrow(() -> new NaoEncontradoException("Ninja com id " + id + " não foi encontrado!")));
    }

    public void deletar(Long id) {
        ninjaRepository.findById(id)
                .orElseThrow(() -> new NaoEncontradoException("Ninja com id " + id + " não foi encontrado!"));
        ninjaRepository.deleteById(id);
    }

}
