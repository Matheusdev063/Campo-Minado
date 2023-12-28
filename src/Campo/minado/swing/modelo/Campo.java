package com.br.Matheus.Campo.minado.Modelo;

import java.util.ArrayList;
import java.util.List;

public class Campo {
	private final int linha; //Posição dos Quadradinhos (----)
	private final int coluna; //posicção dos Quadradinhos( | )
	
	private boolean minado; //boolean sempre inicia com false
	private boolean aberto; // atributo vai estar fechado ou aberto
	private boolean marcado; // Atributo para vê se esta marcado
	
        private List<Campo> vizinhos = new ArrayList<>(); //Array list para adicionar quantos quadrados tem ao lado
	private List<CampoObservador> observadores = new ArrayList<>();  //Lista para criar adicionarr observadores
        //Caso Fossemos utilizar uma Interface do Java sem criar uma nossa propria Interface
        // private List<BiConsumer<Campo,CampoEvento>> observadores2 = new ArrayList<>();
	
        Campo(int linha, int coluna) { //construtor das posições
		this.linha = linha;
		this.coluna = coluna;
	}
        public void registrarObservador(CampoObservador observador){
            observadores.add(observador);
        }
        //Ainda não criamos o evento
        public void notificarObservadores(CampoEvento evento){
            observadores.stream().forEach(o -> o.eventoOcorreu(this, evento));
        }
	//Utilizando Default
	boolean adicionarVizinho(Campo vizinho) { //Adicionar uma logica para não tem como abrir um quadrado que nao seja vizinho
		boolean linhaDiferente = linha != vizinho.linha;
		boolean colunaDiferente = coluna != vizinho.coluna;
		boolean diagonal = linhaDiferente && colunaDiferente;
		
		int deltaLinha = Math.abs(linha - vizinho.linha);
		int deltaColuna = Math.abs(coluna - vizinho.coluna);
		int deltaGeral = deltaColuna + deltaLinha;
		
		if(deltaGeral == 1 && !diagonal) {
			vizinhos.add(vizinho);
			return true;
		}else if(deltaGeral == 2 && diagonal) {
			vizinhos.add(vizinho);
			return true;
		}else {
			return false;
		}
	}
	public void alternarMarcacao() {
		if(!aberto) {
			marcado = !marcado;
                        if(marcado){
                            notificarObservadores(CampoEvento.MARCAR);
                        }else{
                            notificarObservadores(CampoEvento.DESMARCAR);
                        }
		}
	}
	public boolean abrir() {
		if(!aberto && !marcado) {
			if(minado) { //Metod do Explodir
                            notificarObservadores(CampoEvento.EXPLODIR);
                            return true;
			}
                        setAberto(true); //abrir as minas
                            
			if(vizinhancaSegura()) { //função para verificar se em torno tem uma area sem bombas
				vizinhos.forEach(v -> v.abrir()); //lambda para abrir os que estão sem bomba
			}
			return true;
		}else {
		return false;
		}
	}
	public boolean vizinhancaSegura() {
		return vizinhos.stream().noneMatch(v -> v.minado); //localizar quais areas tem nenhuma bomba
	}
	void  minar() {
		minado  = true; // função para ativar o minar
	}
	
	public boolean isMarcado() { //Funciona com Um Setter ou Getter porque está privado
		return marcado;
	}
	
	public void setAberto(boolean aberto) { //set para abrir os quadrados
		this.aberto = aberto;
                if(aberto){ //criei uma Observer para que quando abra ele notifique ao nosso observador
                    notificarObservadores(CampoEvento.ABRIR);
                }
	}
	public boolean isAberto() {
		return aberto;
	}
	public boolean isFechado() {
		return !isAberto();
	}
	public int getLinha() {
		return linha;
	}
	public int getColuna() {
		return coluna;
	}
	boolean objetivoAlcancado() { //Estipular o metodo para que tenha vitoria
		boolean desvendado = !minado && aberto; //precisa nao estar minado e aberto
		boolean protegido = marcado && minado; //marcado e minado
		return desvendado || protegido;	 //retorna a vitoria
	}
	public int minasNaVizinhanca () {
		return (int) vizinhos.stream().filter(v -> v.minado).count(); //Fynção para separar quantidade de minas.
	}
	void reiniciar() {
		aberto = false;
		minado = false;
		marcado = false;
		notificarObservadores(CampoEvento.REINICIAR);
	}
	public boolean isMinado() {
		return minado;
	}
}
