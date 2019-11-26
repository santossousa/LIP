package br.ufc.crateus.util;

public class Node {
	public Token raiz;   //raiz
    public Node esq;   //folha esquerda
    public Node dir;   //folha direita
    
    public Node(Token raiz){
        esq = null;
        this.raiz = raiz;
        dir = null;
    }
    
    public Node(Token raiz, Node esquerda, Node direita){
        esq = esquerda;
        this.raiz = raiz;
        dir = direita;
    }

}
