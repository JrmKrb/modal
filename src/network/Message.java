 p a c k a g e   n e t w o r k ;  
 i m p o r t   j a v a . n i o . B y t e B u f f e r ;  
  
 p u b l i c   c l a s s   M e s s a g e   {  
 	 p u b l i c   s t a t i c   B y t e B u f f e r   b u f f e r F r o m S t r i n g ( S t r i n g   s )   {  
 	 	 i n t   n   =   s . l e n g t h ( ) ;  
 	 	 B y t e B u f f e r   b u f f   =   B y t e B u f f e r . a l l o c a t e ( 2   *   n ) ;  
 	 	 f o r   ( i n t   i   =   0 ;   i   <   n ;   i + + )   {  
 	 	 	 b u f f . p u t C h a r ( s . c h a r A t ( i ) ) ;  
 	 	 }  
 	 	 r e t u r n   b u f f ;  
 	 }  
 }  
