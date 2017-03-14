P <- 1500
i <- 0.01
R <- P/(1-(1+i)^-n)

#to create a vectore we can use c (combine)
a <- c(4:12)

numeri <- 21:1; seq(1,21) #estas dos instrucciones son equivalentes)
seq(1,21,by=2)
seq(3.5,21.4,by=2)
seq(1,21,length=6) #que hace????
a <- c(a,4:1)
a
a[4] #(los índices se especifican usando corchetes).
a[c(2,4)] #(podemos hacer referencia a más de un índice a la vez).
a[1:5]
a[c(T,F)]# que hace???
a[-6] #   wtf
a[a>6]

rep(3,5) # c(i,j) creates i for j times
rep(c(1,4),3)
rep(c(1,4),c(3,2)) 
rep(c(1,4), each=10)
b <- c(2,-2)
2+a
2 * a; 
b*a #(operaciones con funciones de distinta longitud (reciclado)).
