module.exports = function(app)
{
    app.get('/',function(req,res)
    {
        res.send("Home Page");
    });
    
     app.get('/get',function(req,res)
    {
        res.send("Data aagaya!");
    });
    
    app.post('/post',function(req,res)
    { 
        console.log(req.body.name);
        res.send(req.body.name+" is Awesome!");
    });
};
