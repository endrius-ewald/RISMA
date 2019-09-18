# MARS example: Manipulating arrays
# Author:  
# Function: implementa uma funcao que soma dois vetores em um terceiro e retorna o resultado da 
# soma dos elementos dos vetores. Os enderecos dos vetores e da variavel size sao passados como parametros
# atraves da pilha e o resultado do somatorio tambem eh retornado via pilha 
#

        
        .text                   # Add what follows to the text segment of the program
        .globl  main            # Declare the label main to be a global one
main:
		
        	#la      $s0,V1          # register $t0 contains the address of array V1
        #	la	$s1,V2		# register $t1 contains the address of array V2
        	#la      $s2,V3          # register $t2 contains the address of array V3
        #	la 	$s3,size	# get address of size
        	addiu	$sp, $sp, -20
        	sw	$ra, 16($sp)
        	sw	$s1, 12($sp)
        	sw	$s2, 8($sp)
        	sw	$s3, 4($sp)
        	sw	$s0, 0($sp)
        	jal	soma_vet	# chama a subrotina que ira somar os vetores e acumular a soma dos elementos dos vetores
        	lw	$s0, 0($sp)	# armazena o resultado que a funcao retornou via pilha em $s0
        	lw	$ra, 4($sp)
        	addiu	$sp, $sp, 4
        	jr $ra
        	
soma_vet:	lw	$t3, 4($sp)	# get size address
		lw      $t3,0($t3)      # register $t1 contains the size of the array
		lw	$t0, 0($sp)	# get V1 address
		lw	$t1, 12($sp)	# get V2 address
		lw	$t2, 8($sp)	# get V3 address
		addiu	$sp, $sp,16	# 
#	loop:   blez    $t3,end         # if size is/becomes 0, end of processing
	loop:       lw      $t4,0($t0)      # get array element (V1)
	        lw	$t5,0($t1)	# get array element (V2)
	        addu    $t4,$t4,$t5     # add V1+V2
	        addu	$t6, $t6, $t4	# acumula a soma dos elemntos dos vetores e retorna para o pgm principal
	        sw      $t4,0($t2)      # store the result in V3
	        addiu   $t0,$t0,4       # update array pointer (V1). Remember, 1word=4 memory addresses
	        addiu   $t1,$t1,4       # update array pointer (V2). 
	        addiu   $t2,$t2,4       # update array pointer (V3). 
	        addiu   $t3,$t3,-1      # decrement array size counter
	        j       loop            # continue execution
        
	end:    addiu	$sp, $sp, -4
		sw	$t6, 0($sp)	# armazena a soma dos elementos do vetor na pilha
		jr	$ra       

        .data                   # add what follows to the data segment of the program
V1:     .word   0x12 0xff 0x3 0x14 0x878 0x31  0x62 0x10 0x5 0x16 0x20
V2:     .word   0x12 0xff 0x3 0x14 0x878 0x31  0x62 0x10 0x5 0x16 0x20
V3:     .word   0x0  0x0  0x0 0X0  0x0   0x0   0x0  0X0  0x0  0x0  0x0 
                                # Loads the list of ints into successive locations in memory
                                # beginning with location A (i.e. we've initialized an array)
size:   .word   11              # size of the array
