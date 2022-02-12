begin transaction;

-- Creazione dei domini

create domain PosInteger as integer check (value >= 0);

create domain StringaM as varchar(100);

create domain StringaL as varchar(10000);

create domain Denaro as real check (value >=0);

create type tipoLocale as enum ('Pub','Sala concerto','Auditorium');

--Creazione Tabelle--

create table Utente( codiceFiscale StringaM not null,
                      cognome StringaM not null,
                     nome StringaM not null,
                     registrazione  timestamp,
                     primary key(codiceFiscale) );
					 
	
create table Follower(
          utente StringaM not null,
		  primary key (utente),
		  foreign key (utente) references Utente(codiceFiscale)
		 );

create table Nazione(
                nome StringaM not null,
				primary key (nome)
);

create table Citta(
            nome StringaM not null,
			nazione StringaM not null,
			primary key (nome ,nazione),
			foreign key (nazione) references Nazione(nome) 
);

create table Genere(
             nome StringaM,
            primary key (nome)
			);
create table Artista(
           id PosInteger not null,
		   nome StringaM not null,
		   urlSito StringaM ,
		   primary key (id)

);
create table ArtistaInfo(
       artista PosInteger not null,
	   descri StringaL not null,
	   
	   primary key (artista),
	   foreign key (artista) references Artista(id)
 );

create table Spettacolo (
            id PosInteger not null,
			nome StringaM not null,
			durata PosInteger not null,
			genere StringaM not null,
			artista PosInteger not null  ,
             primary key (id),
			 foreign key (genere) references Genere(nome),
			 foreign key (artista) references Artista(id) 
			 
			);
			
create table Locale(
        id PosInteger not null,
        nome StringaM not null,
		descr StringaL ,
		tipo tipoLocale,
		citta StringaM,
		nazione StringaM,
		primary key(id),
		foreign key (citta,nazione) references Citta(nome,nazione)
);
			
create table Concerto(
                inizio timestamp not null,
				spettacolo PosInteger not null, 
				gratis boolean not null,
				locale PosInteger not null references Locale(id) ,
			    importo Denaro not null,
				primary key ( inizio,
				spettacolo) ,
				foreign key (spettacolo) references Spettacolo(id) 
				 
				
				);

create table Prenotazione(
                utente StringaM not null,
                istante timestamp not null,
                concertoinizio timestamp not null,
                concertoSpettacolo PosInteger not null,
                primary key(utente,istante,concertoinizio,concertoSpettacolo) ,
                foreign key (utente) references Utente(codiceFiscale) ,
                foreign key (concertoinizio,concertoSpettacolo) 
				references Concerto(inizio,spettacolo),
		check (istante < concertoinizio)
);


		
create table Posto(
          assegnamento PosInteger not null,
          locale PosInteger not null,
		  primary key (assegnamento,locale),
		  foreign key (locale) references Locale(id) 
);




create table PrePost(
                preUtente StringaM not null,
				preIstante timestamp not null,
				preConcInizio timestamp not null,
				preConSpett PosInteger not null,
				assPosto PosInteger not null,
				locale PosInteger not null,
 				primary key(preUtente,preIstante,preConcInizio,
				            preConSpett,assPosto,locale),
				foreign key(preUtente,preIstante,
				             preConcInizio,preConSpett) references 
							 Prenotazione(utente,istante,concertoinizio,                   
                             concertoSpettacolo)  ,
							 
                foreign key (assPosto,locale) references
				         Posto(assegnamento,locale) 
				);





create table FollowLocale(
               follower StringaM not null,
			   locale PosInteger not null,
			   primary key (follower,locale),
		       foreign key (follower) references Follower(utente) ,
               foreign key (locale) references Locale(id) 

              );

create table FollowArtista(
               follower StringaM not null,
			   artista PosInteger not null,
			   primary key (follower,artista),
		       foreign key (follower) references Follower(utente) ,
               foreign key (artista) references Artista(id) 
     );
create table FollowGenere (
               follower StringaM not null,
			   genere StringaM not null,
			   primary key (follower,genere),
		       foreign key (follower) references Follower(utente) ,
               foreign key (genere) references Genere(nome) 

);

/*
1)v.prenotazionePostoConcerto: una prenotazione di uno o più 
posti per un dato concerto deve essere dello stesso locale dove 
si svolgerà il concerto
*/

CREATE OR REPLACE FUNCTION myFunction() 
RETURNS trigger AS 

$$

BEGIN 
    IF EXISTS( select *
               from Concerto c
               where NEW.preConcinizio = c.inizio and 
                     NEW.preConSpett = c.spettacolo and
                     NEW.locale != c.locale ) then
    RAISE EXCEPTION 'il locale non è quello dove si svolgera il concerto';
    END IF;
    return new;
END;
$$
LANGUAGE 'plpgsql';

create Trigger PrePostConcerto
Before Insert on PrePost
For Each Row
EXECUTE PROCEDURE  myFunction()

----------------------------------------------------------

  
----------------------------------------------------------------

/*
---3v.prenotazionePostiDisponibili: data una prenotazione e un 
insieme di posti  i posti prenotati per quel concerto 
devono essere non gia prenotati .
*/
CREATE OR REPLACE FUNCTION myFunctionPostiDisponibili() 
RETURNS trigger AS 
$$
BEGIN 
    IF EXISTS( select *
               from PrePost pp
               where NEW.preConcinizio = pp.preConcinizio and 
                     NEW.preConSpett = pp.preConSpett and
                     NEW.assPosto = pp.assPosto and 
                     NEW.locale = pp.locale ) then
    RAISE EXCEPTION 'il posto non è disponibile';
    END IF;
    return new;
END;
$$
LANGUAGE 'plpgsql';

create Trigger postiDisponibili
Before Insert on PrePost
For Each Row
EXECUTE PROCEDURE  myFunctionPostiDisponibili()

--------------------------------------------------
/*
vincolo 4) trigger per concerto a pagamento
v.concertoPagamento 
se esiste un istanza di concerto a pagamento
allora l’attributo importo dell’entità Concerto 
deve essere > 0.
*/

---v.concertoPagamento 
CREATE OR REPLACE FUNCTION myFunctionPagamento() 
RETURNS trigger AS 
$$
BEGIN 
    IF NEW.gratis= 'false' and NEW.importo = 0 THEN
         RAISE EXCEPTION 'importo deve essere maggiore di zero';
    END IF;
    return new;
END;
$$
LANGUAGE 'plpgsql';

create Trigger concertoPagamento
Before Insert on Concerto
For Each Row
EXECUTE PROCEDURE  myFunctionPagamento()

----------------------------------------------------------------
/*
vincolo5 trigger per v.concertoGratis
se un concerto è a pagamento allora l’attributo
 l’importo dell’entità deve essere = 0.
*/

CREATE OR REPLACE FUNCTION myFunction() 
RETURNS trigger AS 
$$
BEGIN 
    IF NEW.gratis= 'true' and NEW.importo !=0 THEN
         RAISE EXCEPTION 'importo deve essere zero';
    END IF;
    return new;
END;
$$
LANGUAGE 'plpgsql';

create Trigger concertoGratis
Before Insert on Concerto
For Each Row
EXECUTE PROCEDURE  myFunction()
-------------------------------------------------------------
/*
6)v.concertiSovrapposti: dati due concerti nello 
stesso locale e nello stesso giorno non possono 
essere sovrapposti nel tempo(l’inizio del concerto 
c1  + la durata dello spettacolo di c1 e  l’inizio 
del concerto c2 + la durata dello spettacolo di 
c2 non si sovrappongono)
*/

CREATE OR REPLACE FUNCTION  myFunction()
RETURNS trigger AS 
$$
BEGIN 
    IF EXISTS( select *
               from Concerto c,spettacolo sN,spettacolo sO
               where 
                     NEW.spettacolo = sN.id and
                     c.spettacolo = sO.id and
                     NEW.locale = c.locale and
                     (NEW.inizio , interval '1 minutes' * sN.durata )
                     overlaps (c.inizio ,interval '1 minutes' * sO.durata ) )
               then
    RAISE EXCEPTION 'i concerti si sovrappongono';
    END IF;
    return new;
END;
$$
LANGUAGE 'plpgsql';

create Trigger concertiSovrapposti
Before Insert on Concerto
For Each Row

EXECUTE PROCEDURE  myFunction()





commit;