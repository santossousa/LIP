package br.ufc.crateus.main;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import br.ufc.crateus.analisadorlexico.*;
import br.ufc.crateus.util.NovaException;
//autores : Fábio José ,Everton Cosmo, Joao Vitor, Samuel Artur  
public class Main {
	/*
	 * 1.as expressao do while so sera aceita, com espacamentos e com cada operação entre parentese ex (e > 3) && (m != 7)
	 * 2.o int main foi feito para limitar a declaração de do while, você pode declara 0 a n do while
	 * 
	 * 3.um classe exeption foi defina para erros cometidos pelo o usuario exemplo falta um ;, falta uma } ou {, palavra nao esperada e com as sugestoes de um mini-compilador
	 * 4. uma arvore guarda as exprecoes relacionais
	 * 5.o analisador lexico é o mapeamentos das palavras chaves e simbolos, e operadores relacionais
	 * 6. o analisador sintatico verifica se está coorente com a gramatica implementada.
	 * 7. a clase token guarda os tokens a ser mapeados
	 * observacoes:
	 * não declarar while (r != 3 && t != 4);, as operacoes de com operador logico sao definidos dessa forma, para garantir o balaceamento de parenteses ((exp_relacional ) && (expr_relacional))
	 * 
	 * os testes seram todos em arquivo, para poder simular um analisador sitatico.
	 * 
	 * 
	 * */
	public static void main(String[] args) throws IOException, NovaException {

		File f = new File("exemplo.txt");
		BufferedReader b = new BufferedReader(new FileReader(f));
		while(b.ready()) {
			System.out.println(b.readLine());
		
		}
	
		AnalisadorLexico sc;
		sc = new AnalisadorLexico();

		sc.SCANNER("exemplo.txt"); // chamada ao LÉXICO
		sc.print();
	}

}
