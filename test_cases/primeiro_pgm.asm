 .text   
         .globl  main            
         
 main:  #la	$t0,A		# Carrega endere�o de A em $t0 - PSEUDO-INSTRUCAO
 	lw	$t0,0($t0)	# L� valor de A para $t0
 	#la	$t1,B		# Carrega endere�o de B em $t1 - PSEUDO-INSTRUCAO
 	lw	$t1,0($t1)	# L� valor de B para $t1
 	addu	$t0,$t0,$t1	# $t0 recebe A+B
 	#la	$t2,C		# Carrega endere�o de C em $t2 - PSEUDO-INSTRUCAO
 	sw	$t0,0($t2)	# C recebe A+B
 	
 	
 	
 	jr	$ra		# volta para o kernel do simulador
 	
 	
 	.data
 A:	.word	32
 B:	.word	0x05
 C:	.word	0
        
 
 
