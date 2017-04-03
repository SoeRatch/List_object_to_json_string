import groovy.json.*

// each node is represented with Nope
class Nope{

            public Nope root;
            public Nope child;
            public Nope sibling;
            public String val;           //value of each node is a string
            static int dep=0;            //dep is a helper to reinitialize currentnode
            static Nope currentnode;     //node to keep a track of where to add the incoming node
            def list=[:]


      //constructor to initialize each node with its value i.e a string
            Nope(String va){

                  this.val=va;

            }


      // method to add each incoming node as a child or as a sibling to the currentnode.
            void add(String var){
                      Nope nodetoadd=new Nope(var);
                      if(root==null){               //if incoming node is the first incoming node then this block of if will execute

                              root=nodetoadd;
                      }

                            if(dep==0){             //dep is a helper to reinitialize currentnode
                                  currentnode=root;
                            }

                        if(root!=null){
                                    Nope wheretoadd=currentnode;
                                    doadd(wheretoadd,nodetoadd);   //doadd is the major method to the addition of the incoming node

                          }
              }


              //major method to form the tree structure in child-sibling relationship .It is called by add method.
              // it adds the incoming node as a child or sibling to the appropriate node and updates the current node
              // doesnot add if the incoming node is already being constructed in the tree.In this case it only updates the current node

            void doadd(Nope wheretoadd, Nope addnode){

                     if(wheretoadd.child==null )
                     {
                             if(wheretoadd.val==addnode.val){
                                     currentnode=wheretoadd;
                                     return;
                       }

                        wheretoadd.child=addnode;
                        currentnode=addnode;
                        return;

                        }
                        else{
                                if(wheretoadd.val==addnode.val){
                                        currentnode=wheretoadd;
                                        return;
                        }

                      }
                      while(true){  wheretoadd=wheretoadd.child;
                                    if(wheretoadd.sibling==null)
                                    {
                                       wheretoadd.sibling=addnode;
                                       currentnode=addnode;
                                       return;
                                    }
                                     else{
                                      while(wheretoadd.sibling!=null){
                                        Nope y=wheretoadd.sibling;
                                        if(y.val==addnode.val){
                                                currentnode=y;
                                                return;
                                        }
                                          wheretoadd=wheretoadd.sibling;

                                        }
                                        wheretoadd.sibling=addnode;
                                        currentnode=addnode;
                                        return;
                                    }
                           }

            }


//class to construct in jsonbuilder form
 static class Noqe {
                          String category
                          String path
                          List children
                          }


            //method which takes root of already constructed tree as object i.e roota
            //It also takes map object which maps each node to its path
            // returns a list of children to the object of class Noqe
              List screenout(Nope roota, Map map)
                          {
                          if(roota==null)
                              return;
                          Nope temp=roota;
                          def al=[];                              //List to store all of the child's information

                          // this block runs iteratively for each siblings in the same level
                          while(temp!=null)
                          {
                                  // recursively calling child of each sibling
                                  def Noqe nodex=new Noqe(category:temp.val,path:map[temp.val],children:screenout(temp.child,map));

                                  al.add(nodex);
                                  temp=temp.sibling;              //moving forward to next sibling node
                          }
                          return al;                              //  returning a list  of all the childs to parent.
               }




static void main(String[] args){
def inputFile = new File("input.txt") 							              // take input in list format from input.txt

def inp = new JsonSlurper().parseText(inputFile.text)   		      //parses the  input file

                Iterator<String> ite = inp.iterator();
                def category_path_link=[:];                       //empty map

                // constructing lists of list from inp
                //and constructing map of category to path simultaneously

                def inp_nestedlist=[];
                def temp1=[];
                def temp2=[];
                int sublist_size;
                while(ite.hasNext())
                {
                  temp1=ite.next();
                  temp1=temp1.split("/") as List;
                  sublist_size=temp1.size();
                  while(sublist_size!=0){
                      temp2=temp1.subList(0,sublist_size);
                      temp2=temp2.join('/');
                      category_path_link.put(temp1[sublist_size-1],temp2);
                      sublist_size--;
                  }

                  inp_nestedlist.add(temp1);
                }

                // construction of nested list and map is finished


                Nope obj=new Nope();

                // block of nested for loops to construct the tree where the add method is called recursively
                //each time a string is passed from the list to the add method for which a node is created and contributed to the construction of child sibling tree

                for(int i=0;i<inp_nestedlist.size();i++){
                    Nope.dep=0;

                    for(int j=0;j<inp_nestedlist[i].size();j++){
                        String to_store=inp_nestedlist[i][j].toString();
                        obj.add(to_store);
                        Nope.dep++;
                      }

                  }

                Nope rootie=obj.root;

                  // screenout method to construct a json tree object is called here.
                  def Noqe nodey=new Noqe(category:rootie.val,path:category_path_link[rootie.val],children:obj.screenout(rootie.child,category_path_link));

                  //JsonBuilder constructer takes the tree object as input parameter to print it
                  def done= new JsonBuilder(nodey).toPrettyString()

                  PrintStream out = new PrintStream(new FileOutputStream("output.txt"));   //writes the output in a text file out.print(done)
		                out.print(done)
                    System.setOut(out);

  }

}
