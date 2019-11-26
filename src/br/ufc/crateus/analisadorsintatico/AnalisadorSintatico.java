package br.ufc.crateus.analisadorsintatico;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Scanner;



import br.ufc.crateus.util.Node;
import br.ufc.crateus.util.NovaException;
import br.ufc.crateus.util.Token;

public class AnalisadorSintatico {
	private LinkedList<Token> tokens;
	private LinkedList<String> stringExpressoesList = new LinkedList<>(); // guarda expressões (aritméticas/relacionais)
	private LinkedList<Token> variaveisCharList = new LinkedList<>(); // guarda variáveis Char
	private LinkedList<Token> variaveisIntList = new LinkedList<>(); // guarda variáveis INT
	private LinkedList<Token> variaveisRealList = new LinkedList<>(); // guarda variáveis REAL
	private LinkedList<Token> variaveisList = new LinkedList<>(); // guarda todos os identificadores utilizados no corpo
	private LinkedList<Integer> linhas_IF_List = new LinkedList<>(); // guarda em que linha se encontra o goto nas
																		// expressoes IF e para onde esse goto aponta

// do código (não só os declarados)
	private LinkedList<Boolean> variaveisAuxList = new LinkedList<>(); // guarda um booleano para cada identificador no
																		// corpo do código
	private Token tokenAtual; // o ultimo token a ser lido da fila
	private FileWriter arq;
	private PrintWriter gravarArq;
	private int linha = 1;

	Scanner ler = new Scanner(System.in);

	@SuppressWarnings("unused")
	public void PARSER(LinkedList<Token> tokenFila, String arquivo) throws NovaException, IOException {
		this.tokens = tokenFila;
		tokenAtual = this.tokens.getFirst();

//	AnalisadorSemantico sem;
		// Goto goto_;

		program_(); // Program <id> ;
	
		espaco_opc();
		abre_chave();
		
		// comando_basico();
		// bloco();
		while (!tokenAtual.getId().equals(Token.FECHA_CHAVE)) {
		
			bloco();
			proxToken();
		}
		
		fecha_chave();

	}
	private void EOF() {
		if(tokenAtual.getId().equals(Token.FINAL)) {
			proxToken();
		}
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
			// gravaToken();
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

	private void virgula() throws NovaException { // apenas TOKEN ',' pode passar
		espaco_opc();
		if (tokenAtual.getId().equals(Token.VIRGULA)) {
			// gravaToken();
			proxToken();
			espaco_opc();
		} else {
			throw new NovaException("Erro 2: Símbolo " + tokenAtual.getId() + " inesperado. Esperando: ','. "
					+ "Linha: " + tokenAtual.getPos());
		}
	}



	private void printf() throws NovaException {
		espaco_opc();
		if (tokenAtual.getId().equals(Token.PRINTF)) {
			proxToken();
			espaco_opc();
			abre_parenteses();

			fecha_parenteses();
			pontovirgula();
			proxToken();

		} else {

			throw new NovaException("Erro 2: Símbolo " + tokenAtual.getId() + " inesperado. Esperando: 'printf'. "
					+ "Linha: " + tokenAtual.getPos());

		}

	}

	private void abre_chave() throws NovaException {
		espaco_opc();
		if (tokenAtual.getId().equals(Token.ABRE_CHAVE)) {

			proxToken();
			espaco_opc();
		} else {
			throw new NovaException(
					"Erro: " + tokenAtual.getId() + " inesperado. Esperado '{'" + "linha" + tokenAtual.getPos());
		}
	}

	private void fecha_chave() throws NovaException {
		espaco_opc();
		if (tokenAtual.getId().equals(Token.FECHA_CHAVE)) {
			proxToken();
			espaco_opc();

		} else {

			throw new NovaException(
					"Erro: " + tokenAtual.getId() + "inesperado. Esperado '}'" + "linha" + tokenAtual.getPos());
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

	private void _igual() throws NovaException { // apenas TOKEN '=' pode passar
		espaco_opc();
		if (tokenAtual.getId().equals(Token.DIGUAL)) {
			proxToken();
			espaco_opc();
		} else {
			throw new NovaException("Erro 2: Símbolo " + tokenAtual.getId() + " inesperado. Esperando: '='. "
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

	private void decl_var() throws NovaException { // todo o processo de declaração de variáveis, pode ser repetido
		if (tokenAtual.getId().equals(Token.INT)) {

		
			proxToken();
			espaco_obg();
			variaveisIntList.add(tokenAtual);
			id_(false);
			while (!tokenAtual.getId().equals(";")) {
				virgula();
				variaveisIntList.add(tokenAtual);
				id_(false);
			}
			// pulaLinha();
			proxToken();
			espaco_opc();
		} else if (tokenAtual.getId().equals(Token.FLOAT)) {
			// gravaToken();
			proxToken();
			espaco_obg();
			variaveisRealList.add(tokenAtual);
			id_(false);
			while (!tokenAtual.getId().equals(";")) {
				virgula();
				variaveisRealList.add(tokenAtual);
				id_(false);
			}
			// pulaLinha();
			proxToken();
			espaco_opc();
		} else if (tokenAtual.getId().equals(Token.CHAR)) {
			// gravaToken();
			proxToken();
			espaco_obg();
			variaveisCharList.add(tokenAtual);
			id_(false);
			while (!tokenAtual.getId().equals(";")) {
				virgula();
				variaveisCharList.add(tokenAtual);
				id_(false);
			}
			// pulaLinha();
			proxToken();
			espaco_opc();
		} else {
			throw new NovaException("Erro 2: Símbolo " + tokenAtual.getId()
					+ " inesperado. Esperando: 'INT, Char ou Float'. " + "Linha: " + tokenAtual.getPos());
		}
	}

	private void bloco() throws NovaException { // [<comando> [ <comando>]*] ;

		//comando();

		//while (!tokenAtual.getId().equals(Token.FECHA_CHAVE)) {

			//espaco_opc();
			comando();

		//}


	}

	private void comando() throws NovaException { // <comando_basico> | <iteracao> | if
			DO();			
			While();
		
	}

	private void comando_basico() throws NovaException { // <atribuicao> | <bloco> | ( <id> [, <id>]* );
		Node temp = new Node(null);
	

		espaco_opc();
		if (tokenAtual.getId().equals("ID")) { // <id> = <expr_arit> ;
			variaveisList.add(tokenAtual);
			variaveisAuxList.add(true);
			temp.esq = new Node(tokenAtual);
			proxToken();
			espaco_opc();
			temp.raiz = tokenAtual;
			_igual();
			temp.dir = expr_arit();
			
			while (!stringExpressoesList.isEmpty()) {
				// gravaLinha();
				 stringExpressoesList.pop(); // escreve expressões no arquivo
				// pulaLinha();
			}
			pontovirgula();
			proxToken();
		} else {
			bloco();
		}
	}

	private void DO() throws NovaException {
		espaco_opc();
		if (tokenAtual.getId().equals(Token.DO)) {
			proxToken();
			espaco_opc();
			abre_chave();
			fecha_chave();
		}
		else {
			throw new NovaException("Erro 2: Símbolo " + tokenAtual.getId()
			+ " inesperado. Esperando: 'do'. " + "Linha: " + tokenAtual.getPos());
			
		}

	}
	private void While() throws NovaException {
		espaco_opc();
		if(tokenAtual.getId().equals(Token.WHILE)) {
			espaco_opc();
			dowhile_();
			espaco_opc();
		}
		else {
			throw new NovaException("Erro 2: Símbolo " + tokenAtual.getId()
			+ " inesperado. Esperando: 'while'. " + "Linha: " + tokenAtual.getPos());
			
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
		
	
		linhas_IF_List.add(linha - 1);
		// comando();
		linhas_IF_List.add(linha);
		//proxToken();
	
	

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

	private Node expr_arit() throws NovaException { // retorna uma arvore binaria de expressao aritmetica

		Node arvore = new Node(null);
		Token temp;

		espaco_opc();
		if (e_valor(tokenAtual.getId())) {
			temp = tokenAtual;

			arvore.raiz = temp;
			valor(true);

			if (tokenAtual.getId().equals(Token.ADD) || tokenAtual.getId().equals(Token.SUB)
					|| tokenAtual.getId().equals(Token.DIV) || tokenAtual.getId().equals(Token.MULT)) {

				arvore.raiz = tokenAtual;
				arvore.esq = new Node(temp);
				op_arit();
				arvore.dir = new Node(tokenAtual);
				valor(true);
			}

			return arvore;
		} else {
			abre_parenteses();
			arvore.esq = expr_arit();
			fecha_parenteses();
			arvore.raiz = tokenAtual;
			op_arit();
			abre_parenteses();
			arvore.dir = expr_arit();
			fecha_parenteses();

			return arvore;
		}
	}

	private void op_arit() throws NovaException { // + | - | * | /
		espaco_opc();
		if (tokenAtual.getId().equals(Token.ADD) || tokenAtual.getId().equals(Token.SUB)
				|| tokenAtual.getId().equals(Token.DIV) || tokenAtual.getId().equals(Token.MULT)) {
			proxToken();
			espaco_opc();
		} else {
			throw new NovaException("Erro 7: Operador " + tokenAtual.getId()
					+ " invalido. Esperando: 'Operador aritmetico'. " + "Linha: " + tokenAtual.getPos());
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
