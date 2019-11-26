package br.ufc.crateus.main;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import br.ufc.crateus.analisadorlexico.*;
import br.ufc.crateus.util.NovaException;

public class Main {
	public static void main(String[] args) throws IOException, NovaException {
		String nome;
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
