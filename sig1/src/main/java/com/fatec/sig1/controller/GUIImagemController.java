package com.fatec.sig1.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.fatec.sig1.model.Imagem;
import com.fatec.sig1.services.MantemImagem;

@Controller
@RequestMapping(path = "/sig/imagens")
public class GUIImagemController {
	@Autowired
	MantemImagem servicoMantemImagem;
	@CrossOrigin
	@GetMapping
	public ModelAndView exibirFormulario(MultipartFile file1) {
		ModelAndView mv = new ModelAndView("uploadImagens");
		mv.addObject("file1", new Imagem());
		return mv;
	}
	@CrossOrigin
	@PostMapping("/upload")
	public ModelAndView uploadArquivo(@RequestParam("file1") MultipartFile arquivo) {
		ModelAndView mv = new ModelAndView("uploadImagens");
		Logger logger = LogManager.getLogger(this.getClass());
		logger.info(">>>>>> gui controller imagem chamado");
		if (!arquivo.isEmpty()) {
			logger.info(">>>>>> manipula file upload file nao esta vazio");
			try {
				servicoMantemImagem.salvar(arquivo);
			} catch (Exception e) {
				logger.info(">>>>>> gui controller erro => " + e.getMessage());
			}
		}else {
			mv.addObject("message", "Dados invalidos");
		}
		return mv;
	}
	@GetMapping("/consulta")
	public ModelAndView exibirFormularioConsulta() {
		return new ModelAndView("consultarProduto");
	}
}
