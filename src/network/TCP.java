 p a c k a g e   n e t w o r k ;  
  
 i m p o r t   j a v a . i o . F i l e ;  
 i m p o r t   j a v a . i o . F i l e I n p u t S t r e a m ;  
 i m p o r t   j a v a . i o . F i l e N o t F o u n d E x c e p t i o n ;  
 i m p o r t   j a v a . i o . I O E x c e p t i o n ;  
 i m p o r t   j a v a . n e t . I n e t S o c k e t A d d r e s s ;  
 i m p o r t   j a v a . n i o . B y t e B u f f e r ;  
 i m p o r t   j a v a . n i o . c h a n n e l s . F i l e C h a n n e l ;  
 i m p o r t   j a v a . n i o . c h a n n e l s . S e r v e r S o c k e t C h a n n e l ;  
 i m p o r t   j a v a . n i o . c h a n n e l s . S o c k e t C h a n n e l ;  
  
 p u b l i c   c l a s s   T C P   {  
 	 S e r v e r S o c k e t C h a n n e l   s e r v e r S o c k e t ;  
 	 S o c k e t C h a n n e l   c l i e n t S o c k e t ;  
 	  
 	 p u b l i c   T C P ( S t r i n g   i p ,   i n t   p o r t )   {  
 	 	 c o n n e c t C l i e n t ( i p , p o r t ) ;  
 	 }  
 	  
 	 p u b l i c   v o i d   c o n n e c t C l i e n t ( S t r i n g   i p ,   i n t   p o r t )   {  
 	 	 t r y   {  
 	 	 	 I n e t S o c k e t A d d r e s s   d e s t   =   n e w   I n e t S o c k e t A d d r e s s ( i p , p o r t ) ;  
 	 	 	 c l i e n t S o c k e t   =   S o c k e t C h a n n e l . o p e n ( ) ;  
 	 	 	 c l i e n t S o c k e t . c o n n e c t ( d e s t ) ;  
 	 	 }   c a t c h   ( I O E x c e p t i o n   e )   {  
 	 	 	 S y s t e m . o u t . p r i n t l n ( " E R R E U R   c o n n e c t C l i e n t   :   " + i p + " : " + p o r t ) ;  
 	 	 	 e . p r i n t S t a c k T r a c e ( ) ;  
 	 	 }  
 	 	  
 	 }  
  
 	 / /   F o r   C l i e n t  
 	 p u b l i c   v o i d   s e n d C l a s s ( S t r i n g   p a t h )   t h r o w s   I O E x c e p t i o n   {  
 	 	 F i l e C h a n n e l   f i s C ;  
 	 	 t r y   {  
 	 	 	 F i l e I n p u t S t r e a m   f i s   =   n e w   F i l e I n p u t S t r e a m ( n e w   F i l e ( p a t h ) ) ;  
 	 	 	 f i s C   =   f i s . g e t C h a n n e l ( ) ;  
 	 	 	 B y t e B u f f e r   b u f f   =   B y t e B u f f e r . a l l o c a t e ( ( i n t )   f i s C . s i z e ( ) ) ;  
 	 	 	 f i s C . r e a d ( b u f f ) ;  
 	 	 	 c l i e n t S o c k e t . w r i t e ( b u f f ) ;  
 	 	 	 f i s C . c l o s e ( ) ;  
 	 	 	 f i s . c l o s e ( ) ;  
 	 	 }   c a t c h   ( F i l e N o t F o u n d E x c e p t i o n   e )   {  
 	 	 	 S y s t e m . o u t . p r i n t l n ( " F i c h e r   " + p a t h + " n o n   t r o u v �   ! " ) ;  
 	 	 	 e . p r i n t S t a c k T r a c e ( ) ;  
 	 	 }  
 	 }  
 	  
 	  
 	 / /   F o r   C l i e n t  
 	 p u b l i c   v o i d   s e n d S e r i a l i z e d C l a s s ( S t r i n g   p a t h )   { }  
 	  
 	 / /   F o r   C l i e n t   &   S e r v e r s  
 	 p u b l i c   v o i d   a c k ( )   { }  
 	  
 	 / /   F o r   S e r v e r s  
 	 p u b l i c   v o i d   s e n d R e s u l t ( )   { }  
 	  
 	 / /   F o r   C l i e n t s  
 	 p u b l i c   v o i d   e n d O f T a s k ( )   {  
 	 	  
 	 }  
 	  
 }  
