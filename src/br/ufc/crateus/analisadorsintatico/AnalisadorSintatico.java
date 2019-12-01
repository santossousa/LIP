package br.ufc.crateus.analisadorsintatico;

import java.io.IOException;

import java.util.LinkedList;
import java.util.Scanner;

import br.ufc.crateus.util.Node;
import br.ufc.crateus.util.NovaException;
import br.ufc.crateus.util.Token;

public class AnalisadorSintatico {
	private LinkedList<Token> tokens;
	private LinkedList<String> stringExpressoesList = new LinkedList<>(); // guarda expressões (aritméticas/relacionai
	private LinkedList<Token> variaveisList = new LinkedList<>(); // guarda todos os identificadores utilizados no corpo
	private LinkedList<Integer> linhas_while_List = new LinkedList<>(); // guarda em que linha se encontra o goto nas
																		// expressoes IF e para onde esse goto aponta

// do código (não só os declarados)
	private LinkedList<Boolean> variaveisAuxList = new LinkedList<>(); // guarda um booleano para cada identificador no
																		// corpo do código
	private Token tokenAtual; // o ultimo token a ser lido da fila

	private int linha = 1;

	Scanner ler = new Scanner(System.in);

	
	public void PARSER(LinkedList<Token> tokenFila, String arquivo) throws NovaException, IOException {
		this.tokens = tokenFila;
		tokenAtual = this.tokens.getFirst();

		program_(); // int <id> {}

		espaco_opc();
		abre_chave();
		while (!tokenAtual.getId().equals(Token.FECHA_CHAVE)) {

			bloco();
			proxToken();
		}

		fecha_chave();

	}

	private void proxToken() { // remove o TOKEN atual da fila e pega o proximo
		int linha;

		linha = tokenAtual.getPos();
		tokens.pop();

		if (tokens.isEmpty())
			tokenAtual = new Token(Token.FINAL, "", linha);
		else
			tokenAtual = tokens.getFirst();
	}

	private void valor(boolean ehExpressao) throws NovaException { // apenas TOKEN 'ID ou NUMERICO' podem passar
		espaco_opc();
		if (tokenAtual.getId().equals("NUMERICO")) {
			proxToken();
			espaco_opc();
		} else if (tokenAtual.getId().equals("ID")) {
			variaveisList.add(tokenAtual);
			variaveisAuxList.add(ehExpressao);
			proxToken();
			espaco_opc();
		} else {
			throw new NovaException("Erro 2: Símbolo " + tokenAtual.getId()
					+ " inesperado. Esperando: 'ID ou NUMERICO'. " + "Linha: " + tokenAtual.getPos());
		}
	}

	private void id_(boolean ehExpressao/* esse token está em uma expressão? */) throws NovaException { // apenas TOKEN
																										// 'ID' pode
																										// passar
		espaco_opc();
		if (tokenAtual.getId().equals("ID")) {
			variaveisList.add(tokenAtual);
			variaveisAuxList.add(ehExpressao);
			proxToken();
			espaco_opc();
		} else {
			throw new NovaException("Erro 2: Símbolo " + tokenAtual.getId() + " inesperado. Esperando: 'ID'. "
					+ "Linha: " + tokenAtual.getPos());
		}
	}

	private boolean e_valor(String compara) throws NovaException { // retorna TRUE caso o token seja 'ID ou NUMERICO'
		if (compara.equals("NUMERICO") || compara.equals("ID")) {
			return true;
		} else {
			return false;
		}
	}

	private void pontovirgula() throws NovaException { //// apenas TOKEN ';' pode passar
		espaco_opc();
		if (!tokenAtual.getId().equals(Token.PONTOVIRGULA)) {
			throw new NovaException("Erro 2: Símbolo " + tokenAtual.getId() + " inesperado. Esperando: ';'. "
					+ "Linha: " + tokenAtual.getPos());
		} else {

		}
	}

	

	private void abre_chave() throws NovaException {
		espaco_opc();
		if (tokenAtual.getId().equals(Token.ABRE_CHAVE)) {

			proxToken();
			espaco_opc();
		} else {
			throw new NovaException(
					"Erro: " + tokenAtual.getId() + " inesperado. Esperado '{' " + "linha" + tokenAtual.getPos());
		}
	}

	private void fecha_chave() throws NovaException {
		espaco_opc();
		if (tokenAtual.getId().equals(Token.FECHA_CHAVE)) {
			proxToken();
			espaco_opc();

		} else {

			throw new NovaException(
					"Erro: " + tokenAtual.getId() + " inesperado. Esperado '}' " + "linha" + tokenAtual.getPos());
		}
	}

	private void abre_parenteses() throws NovaException { // apenas TOKEN '(' pode passar
		espaco_opc();
		if (tokenAtual.getId().equals(Token.ABRE_PARENTESES)) {

			proxToken();
			espaco_opc();
		} else {
			throw new NovaException("Erro 2: Símbolo " + tokenAtual.getId() + " inesperado. Esperando: '('. "
					+ "Linha: " + tokenAtual.getPos());
		}
	}

	private void fecha_parenteses() throws NovaException { // apenas TOKEN ')' pode passar
		espaco_opc();
		if (tokenAtual.getId().equals(Token.FECHA_PARENTESES)) {
			proxToken();
			espaco_opc();
		} else {
			throw new NovaException("Erro 2: Símbolo " + tokenAtual.getId() + " inesperado. Esperando: ')'. "
					+ "Linha: " + tokenAtual.getPos());
		}
	}

	private void program_() throws NovaException { //
		espaco_opc();
		if (tokenAtual.getId().equals(Token.INT)) {

			proxToken();
			espaco_obg();

			id_(false);
			variaveisList.pop();
			variaveisAuxList.pop();
			abre_parenteses();
			fecha_parenteses();

		} else {
			throw new NovaException("Erro 2: Símbolo " + tokenAtual.getId() + " inesperado. Esperando: 'INT'. "
					+ "Linha: " + tokenAtual.getPos());
		}
	}

	private void bloco() throws NovaException { // [<comando> [ <comando>]*] ;

		comando();

	}

	private void comando() throws NovaException { // | <iteracao> | Do While
		DO();
		While();

	}

	private void DO() throws NovaException {
		espaco_opc();
		if (tokenAtual.getId().equals(Token.DO)) {
			proxToken();
			espaco_opc();
			abre_chave();
			fecha_chave();
		} else {
			throw new NovaException("Erro 2: Símbolo " + tokenAtual.getId() + " inesperado. Esperando: 'do'. "
					+ "Linha: " + tokenAtual.getPos());

		}

	}

	private void While() throws NovaException {
		espaco_opc();
		if (tokenAtual.getId().equals(Token.WHILE)) {
			espaco_opc();
			dowhile_();
			espaco_opc();
		} else {
			throw new NovaException("Erro 2: Símbolo " + tokenAtual.getId() + " inesperado. Esperando: 'while'. "
					+ "Linha: " + tokenAtual.getPos());

		}
	}

	private void dowhile_() throws NovaException { // doWhile (<expr_relacional>);
		Node temp = new Node(null);

		temp.raiz = tokenAtual;

		proxToken();
		espaco_opc();
		abre_parenteses();

		temp.dir = expr_relacional();
		while (!stringExpressoesList.isEmpty()) {

			stringExpressoesList.pop();
		}
		fecha_parenteses();
		pontovirgula();

		linhas_while_List.add(linha - 1);
		linhas_while_List.add(linha);

	}

	private Node expr_relacional() throws NovaException { // retorna uma arvore binaria de expressao relacional

		Node arvore = new Node(null);

		espaco_opc();
		if (e_valor(tokenAtual.getId())) {
			arvore.esq = new Node(tokenAtual);
			valor(true);
			arvore.raiz = tokenAtual;
			op_relacionais();
			arvore.dir = new Node(tokenAtual);
			valor(true);

			return arvore;
		} else {
			abre_parenteses();
			arvore.esq = expr_relacional();
			fecha_parenteses();
			do {
				arvore.raiz = tokenAtual;
				proxToken();
				abre_parenteses();
				arvore.dir = expr_relacional();
				fecha_parenteses();
				if (tokenAtual.getId().equals(Token.AND) || tokenAtual.getId().equals(Token.OR)) {
					arvore.esq = new Node(arvore.raiz, arvore.esq, arvore.dir);
					arvore.raiz = null;
					arvore.dir = null;
				}
			} while (tokenAtual.getId().equals(Token.AND) || tokenAtual.getId().equals(Token.OR));

			return arvore;
		}
	}

	private void op_relacionais() throws NovaException { // < | > | <= | >= | = | !=
		espaco_opc();
		if (tokenAtual.getId().equals(Token.MENORQ) || tokenAtual.getId().equals(Token.MAIORQ)
				|| tokenAtual.getId().equals(Token.MENORQ_IGUAL) || tokenAtual.getId().equals(Token.MAIORQ_IGUAl)
				|| tokenAtual.getId().equals(Token.IGUAL) || tokenAtual.getId().equals(Token.DIFERENTE)) {
			proxToken();
			espaco_opc();
		} else {
			throw new NovaException("Erro 7: Operador " + tokenAtual.getId()
					+ " invalido. Esperando: 'Operador relacional'. " + "Linha: " + tokenAtual.getPos());
		}
	}

	private void espaco_opc() { // se houver um espaço, considera como um espaço opcional
		if (tokenAtual.getId().equals(Token.ESPACO)) {
			proxToken();
		}
	}

	private void espaco_obg() throws NovaException { // se houver um espaço, considera como um espaço obrigatório
		if (tokenAtual.getId().equals(Token.ESPACO)) {
			proxToken();
		} else {
			throw new NovaException("Erro 2: Símbolo " + tokenAtual.getId() + " inesperado. Observar o espacamento. "
					+ "Linha: " + tokenAtual.getPos());
		}
	}

}
